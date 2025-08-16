package com.shop.controller;


import cn.hutool.json.JSONUtil;
import com.shop.entity.EventType;
import com.shop.result.Result;
import com.shop.service.EventTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class TypeController {
    @Autowired
    private EventTypeService eventTypeService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @GetMapping("/type")
    public Result<List<EventType>> getAllType(){
        String type = stringRedisTemplate.opsForValue().get("type");
        if(type != null){
            List<EventType> list = JSONUtil.toList(type, EventType.class);
            return Result.success( list);
        }
        List<EventType> list = eventTypeService.list();
        String jsonStr = JSONUtil.toJsonStr(list);
        stringRedisTemplate.opsForValue().set("type", jsonStr);
        return Result.success(list);
    }

}
