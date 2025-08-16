package com.shop.enums;

public enum  SaleStatus implements  BaseEnum{
    NOT_OPENED(0, "未开售"),
    PRE_SALE(1, "预售中"),
    SOLD_OUT(3, "已售罄"),

    ;
    private final Integer code;
    private final String message;
    SaleStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
