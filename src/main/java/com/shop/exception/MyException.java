package com.shop.exception;

import com.shop.result.ResultCodeEnum;
import lombok.Data;


@Data
public class MyException extends RuntimeException{
    private Integer code;

    public MyException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
    }
}
