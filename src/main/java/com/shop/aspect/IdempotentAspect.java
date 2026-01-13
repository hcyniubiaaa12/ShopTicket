package com.shop.aspect;

import com.shop.exception.GlobalException;
import com.shop.exception.MyException;
import com.shop.result.ResultCodeEnum;
import com.shop.utils.Idempotent;
import com.shop.vo.OrdersVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class IdempotentAspect {
    private static final Logger log = LoggerFactory.getLogger(IdempotentAspect.class);
    @Autowired
    private StringRedisTemplate redisTemplate;
    // SPEL 表达式解析器
    private final SpelExpressionParser parser = new SpelExpressionParser();
    // 用来寻找参数名
    private final ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(idempotent)")
    public Object checkIdempotent(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        //获取参数值
        Object[] args = joinPoint.getArgs();
        //获取注解的key
        String key = idempotent.key();
        //解析SPEL表达获取唯一key
        String businessKey = parseKey(key, method, args);
        String redisKey = "idempotent:" + businessKey;
        //当获取成功才能执行方法,不然为规定时间内重复请求
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(redisKey, "2", idempotent.expire(), TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(flag)) {
            throw new MyException(ResultCodeEnum.REPEAT_OPERATION);
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            redisTemplate.delete(redisKey);
            throw new RuntimeException(e);
        }


    }

    public String parseKey(String key, Method method, Object[] args) {
        // 获取方法上的参数名 (例如: ["req", "userId"])

        String[] paramNames = discoverer.getParameterNames(method);

        // 准备 SPEL 上下文
        EvaluationContext context = new StandardEvaluationContext();
        // 将 参数名 和 参数值 对应放入上下文
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        // 解析 SPEL 表达式
        Expression expression = parser.parseExpression(key);
        Object value = expression.getValue(context);
        try {
            return Objects.requireNonNull(value).toString();
        } catch (Exception e) {
            throw new RuntimeException("SPEL expression parsing failed: " + key, e);
        }

    }
}
