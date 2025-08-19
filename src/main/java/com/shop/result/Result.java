package com.shop.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class  Result<T> {
    private Integer code;
    private String message;
    private T data;

    public Result(ResultCodeEnum resultCodeEnum, T data){
        this.code = resultCodeEnum.getCode();
        this.message = resultCodeEnum.getMessage();
        this.data = data;
    }
    public Result(Integer code, String message){
        this.code = code;
        this.message = message;

    }
    public static <T> Result<T> success(T data){
        return new Result<>(ResultCodeEnum.SUCCESS, data);
    }
    public static <T> Result<T> fail(T data){
        return new Result<>(ResultCodeEnum.FAIL, data);
    }
    public static <T> Result<T> fail(Integer code, String message){
        return new Result<>(code, message);
    }

}
