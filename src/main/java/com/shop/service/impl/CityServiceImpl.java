package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.dto.CityDto;
import com.shop.entity.City;
import com.shop.service.CityService;
import com.shop.mapper.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 陈增
* @description 针对表【city】的数据库操作Service实现
* @createDate 2025-08-15 18:04:39
*/
@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City>
    implements CityService{
    @Autowired
    private CityMapper cityMapper;

    @Override
    public List<CityDto> getCityByEventId(Integer id) {
        return cityMapper.getCityByEventId(id);
    }
}




