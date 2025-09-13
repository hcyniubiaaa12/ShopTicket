package com.shop.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum  SaleStatus implements  BaseEnum{
    NOT_OPENED(1, "未开售"),
    PRE_SALE(2, "售卖中"),
    SALE_ENDED(3, "停止售卖"),


    ;
    @EnumValue
    private final Integer code;
    private final String message;
    SaleStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    @JsonValue
    public Integer getCode() {
        return this.code;
    }

    @Override

    public String getMessage() {
        return this.message;
    }
}
