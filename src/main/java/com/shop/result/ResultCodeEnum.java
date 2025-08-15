package com.shop.result;

public enum ResultCodeEnum {
    SUCCESS(200, "成功"),

    FAIL(500, "失败"),
    DISABLE(403, "该账户已经被禁用"),
    NOT_EXIT(404, "验证码不存在"),
    EXPIRED(405, "验证码已过期"),
    NOT_MATCH(406, "验证码错误"),
    PWD_ERROR(407, "密码错误"),
    ROLE_ERROR(408, "权限不足");


    private final Integer code;
    private final String message;


    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}