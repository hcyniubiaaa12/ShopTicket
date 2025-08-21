package com.shop.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.shop.entity.User;
import com.shop.userhold.UserHold;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RefreshInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;

    public RefreshInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null) {
            return true;
        }
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries("user:" + token);
        User user = BeanUtil.fillBeanWithMap(entries, new User(), false);
        UserHold.setUser(user.getId());
        stringRedisTemplate.expire("user:" + token, 30, TimeUnit.MINUTES);
        return true;


    }

}

