package com.shop.mapper;

import com.shop.entity.Performances;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.dto.PerformanceDto;
import com.shop.dto.TimeDto;

import java.util.List;

/**
* @author 陈增
* @description 针对表【performances】的数据库操作Mapper
* @createDate 2025-08-15 18:05:34
* @Entity com.shop.entity.Performances
*/
public interface PerformancesMapper extends BaseMapper<Performances> {

    List<PerformanceDto> getAllPerformance();

    List<PerformanceDto> getPerformanceById(Integer id);

    List<PerformanceDto> getPerformanceByEventId(Integer id,Integer cityId);

    List<TimeDto> getTimeByEventId(Integer id,Integer cityId);

    List<PerformanceDto> getMyPerformance(Integer userId);
}




