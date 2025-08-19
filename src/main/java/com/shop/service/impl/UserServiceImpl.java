package com.shop.service.impl;

import cn.hutool.json.JSONUtil;
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

import java.util.concurrent.TimeUnit;

/**
 * @author 陈增
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2025-08-20 14:52:48
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String MOBILE_REGEX = "^1[3-9]\\d{9}$";


    @Override
    public Result login(CaptchaVo captchaVo) {
        String account = captchaVo.getAccount();
        String code = captchaVo.getCode();
        String key = captchaVo.getKey();
        String value = redisTemplate.opsForValue().get("captcha:"+key);
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
            super.save(user);
            return Result.success(null);
        }
        redisTemplate.opsForValue().set("user:" + user.getId(), JSONUtil.toJsonStr(user), 40, TimeUnit.MINUTES);

        return Result.success(user);
    }
}




