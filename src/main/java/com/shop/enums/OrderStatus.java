package com.shop.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus implements BaseEnum {
    PENDING(1, "待支付"),

    PAID(2, "已支付"),

    CANCELED(3, "已取消");

    @EnumValue
    private final Integer code;
    private final String message;

    OrderStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override


    public Integer getCode() {
        return this.code;
    }

    @Override
    @JsonValue
    public String getMessage() {
        return this.message;
    }
}
