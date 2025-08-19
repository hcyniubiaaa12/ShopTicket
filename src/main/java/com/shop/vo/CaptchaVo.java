package com.shop.vo;

import lombok.Data;

@Data
public class CaptchaVo {
    private String account;
    private String key;
    private String code;

}
