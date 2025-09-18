package com.shop.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.dto.CityDto;
import com.shop.entity.City;
import com.shop.result.Result;
import com.shop.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController

public class CityController {
    @Autowired
    private CityService cityService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/city")
    public Result<List<City>> getCity() {
        String city = redisTemplate.opsForValue().get("city");
        if (city != null) {
            List<City> list = JSONUtil.toList(city, City.class);
            return Result.success(list);
        }
        QueryWrapper<City> cityQueryWrapper = new QueryWrapper<>();
        cityQueryWrapper.select("name", "id");
        List<City> cityList = cityService.list(cityQueryWrapper);
        redisTemplate.opsForValue().set("city", JSONUtil.toJsonStr(cityList));



        return Result.success(cityList);
    }

    @GetMapping("/city/{id}")
    public Result<List<CityDto>> getCityByEventIdId(@PathVariable Integer id) {
        List<CityDto> list = cityService.getCityByEventId(id);
        return Result.success(list);
    }
}
