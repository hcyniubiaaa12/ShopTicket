package com.shop.service;

import com.shop.dto.CityDto;
import com.shop.entity.City;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 陈增
* @description 针对表【city】的数据库操作Service
* @createDate 2025-08-15 18:04:39
*/
public interface CityService extends IService<City> {

    List<CityDto> getCityByEventId(Integer id);
}
