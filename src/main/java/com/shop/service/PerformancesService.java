package com.shop.service;

import com.shop.entity.Performances;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.dto.PerformanceDto;
import com.shop.dto.TimeDto;

import java.util.List;

/**
* @author 陈增
* @description 针对表【performances】的数据库操作Service
* @createDate 2025-08-15 18:05:34
*/
public interface PerformancesService extends IService<Performances> {

   List<PerformanceDto> getPerformanceById(Integer id);

   List<PerformanceDto> getAllPerformance();

    List<PerformanceDto> getPerformanceByEventId(Integer id,Integer cityId);

    List<TimeDto> getTimeByEventId(Integer id,Integer cityId);
}
