package com.shop.exception;

import com.shop.result.Result;
import com.shop.result.ResultCodeEnum;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(MyException.class)
    public Result handleException(MyException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }
}
