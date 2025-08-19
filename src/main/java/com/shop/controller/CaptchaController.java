package com.shop.controller;

import com.shop.dto.CaptchaDto;
import com.shop.result.Result;
import com.shop.vo.CaptchaVo;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@RestController
public class CaptchaController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping("/getCaptcha")
    public Result <CaptchaDto> getCaptcha() {
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        String verCode = specCaptcha.text().toLowerCase();
        String key = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set("captcha:"+key,verCode,2, TimeUnit.MINUTES);
        String img = specCaptcha.toBase64();
        CaptchaDto captchaDto = new CaptchaDto(key, img);
        return Result.success(captchaDto);

    }
}
