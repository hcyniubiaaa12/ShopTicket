package com.shop.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TicketStatus implements BaseEnum {
    UNUSED(0, "待观看"),

    USED(1, "已观看");

    @EnumValue
    private final Integer code;
    private final String message;

    TicketStatus(Integer code, String message) {
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
