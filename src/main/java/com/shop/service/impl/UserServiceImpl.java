package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.User;
import com.shop.exception.MyException;
import com.shop.result.Result;
import com.shop.result.ResultCodeEnum;
import com.shop.service.UserService;
import com.shop.mapper.UserMapper;
import com.shop.vo.CaptchaVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 陈增
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-08-21 17:07:14
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    private static final Random RANDOM = new Random();
    private static final String[] WORDS = {"小", "星", "雨", "梦", "雪", "风", "月", "花", "心", "阿"};
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String MOBILE_REGEX = "^1[3-9]\\d{9}$";

    public static String generateRandomName() {
        StringBuilder name = new StringBuilder();
        int length = 2 + RANDOM.nextInt(3); // 2, 3, 或 4

        while (name.length() < length) {
            if (RANDOM.nextBoolean() && name.length() < length - 1) {
                name.append(RANDOM.nextInt(99) + 1); // 加 1~99 的数字
            } else {
                name.append(WORDS[RANDOM.nextInt(WORDS.length)]);
            }
        }

        return name.toString().substring(0, Math.min(4, name.length()));
    }


    @Override
    public Result login(CaptchaVo captchaVo) {
        String account = captchaVo.getAccount();
        String code = captchaVo.getCode();
        String key = captchaVo.getKey();
        String value = redisTemplate.opsForValue().get("captcha:" + key);
        if (!account.matches(MOBILE_REGEX)) {
            throw new MyException(ResultCodeEnum.DISABLE);
        }
        if (code.isEmpty()) {
            throw new MyException(ResultCodeEnum.NOT_EXIT);
        }
        if (value == null) {
            throw new MyException(ResultCodeEnum.EXPIRED);
        }
        if (!value.equals(code)) {
            throw new MyException(ResultCodeEnum.NOT_MATCH);
        }
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);
        User user = super.getOne(wrapper);
        if (user == null) {
            user = new User();
            user.setAccount(account);
            user.setName(generateRandomName());
            super.save(user);
            return Result.success(null);
        }
        String token = UUID.randomUUID().toString();
        HashMap<String, String> map = new HashMap<>();
        map.put("id", user.getId().toString());
        map.put("account", user.getAccount());
        map.put("name", user.getName());
        map.put("url", user.getUrl());
        redisTemplate.opsForHash().putAll("user:" + token, map);
        redisTemplate.expire("user:" + token, 30, TimeUnit.MINUTES);


        return Result.success(token);
    }
}




