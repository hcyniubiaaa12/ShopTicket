package com.shop.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class GenerateId {

    private final StringRedisTemplate stringRedisTemplate;
    public GenerateId( StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    public long generateId(String key) {

        LocalDateTime now = LocalDateTime.now();
        long time = now.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String Date = now.format(dateTimeFormatter);
        Long increment = stringRedisTemplate.opsForValue().increment(key + ":" + Date);

        return time << 24 | increment;
    }
}
