package com.shop.aspect;

import com.shop.exception.GlobalException;
import com.shop.exception.MyException;
import com.shop.result.ResultCodeEnum;
import com.shop.utils.Idempotent;
import com.shop.vo.OrdersVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class IdempotentAspect {
    private static final Logger log = LoggerFactory.getLogger(IdempotentAspect.class);
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("@annotation(idempotent)")
    public Object checkIdempotent(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        log.info("开始执行方法");
        Object[] args = joinPoint.getArgs();
        String idValue;

        if (args[0] instanceof Long) {
            idValue = args[0].toString();
        } else if (args[0] instanceof OrdersVo) {
            Method getUserId = args[0].getClass().getMethod("getUserId");
            Object invoke = getUserId.invoke(args[0]);
            idValue = invoke.toString();

        } else {
            Method getId = args[0].getClass().getMethod("getId");
            Object invoke = getId.invoke(args[0]);
            idValue = invoke.toString();
        }
        String key = idempotent.value();
        Boolean b = redisTemplate.opsForValue().setIfAbsent("idempotent:" + key + ":" + idValue, "process", idempotent.expire(), TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(b)) {
            return joinPoint.proceed();
        } else {
            throw new MyException(ResultCodeEnum.REPEAT_OPERATION);
        }


    }
}
