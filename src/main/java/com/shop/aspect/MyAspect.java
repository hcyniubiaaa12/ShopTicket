package com.shop.aspect;

import com.shop.result.Result;
import com.shop.userhold.UserHold;
import jakarta.annotation.PostConstruct;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Aspect
public class MyAspect {
    private static final Logger log = LoggerFactory.getLogger(MyAspect.class);


    @Around("execution(* com.shop.controller.OrderController.*(..)) && !execution(* com.shop.controller.OrderController.queryOrder(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("开始执行方法:{}", joinPoint.getSignature().getName());
        switch (joinPoint.getSignature().getName()){
            case "saveOrder":
                log.info("用户: {}下单，订单参数: {}", UserHold.getUser(),Arrays.toString(joinPoint.getArgs()));
                break;
            case "cancel":
                log.info("用户: {}取消订单，订单ID: {}",UserHold.getUser(),Arrays.toString(joinPoint.getArgs()));
                break;
            case "pay":
                log.info("用户:{}支付，订单ID: {}",UserHold.getUser(), Arrays.toString(joinPoint.getArgs()));
                break;

        }

        long start = System.currentTimeMillis();
        Object result = null;

        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("用户: {}方法【{}】执行异常: {}", UserHold.getUser(),joinPoint.getSignature().getName(), e.getMessage());
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            log.info("用户: {}方法【{}】执行完毕，耗时: {} ms",UserHold.getUser(), joinPoint.getSignature().getName(), end - start);
        }
        return result;
    }
}
