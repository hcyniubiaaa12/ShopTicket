package com.shop.enums;

public enum PerformanceStatus implements BaseEnum{
    UNDERWAY(1, "进行中"),
    FINISHED(2, "已结束"),
    CANCELED(3, "已取消");
    private final Integer code;
    private final String message;
    PerformanceStatus(Integer code, String message) {
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
