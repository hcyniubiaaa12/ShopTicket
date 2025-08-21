package com.shop.controller;

import com.shop.entity.User;
import com.shop.result.Result;
import com.shop.service.UserService;
import com.shop.vo.CaptchaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController

public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login (@RequestBody CaptchaVo captchaVo){

        return  userService.login(captchaVo);
    }
}
