package com.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.entity.City;
import com.shop.result.Result;
import com.shop.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController

public class CityController {
    @Autowired
    private CityService cityService;
    @GetMapping("/city")
    public Result<List<City>> getCity() {
        QueryWrapper<City> cityQueryWrapper = new QueryWrapper<>();
        cityQueryWrapper.select( "name", "id");
        List<City> cityList = cityService.list(cityQueryWrapper);
        return Result.success(cityList);
    }
}
