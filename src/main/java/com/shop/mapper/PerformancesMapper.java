package com.shop.mapper;

import com.shop.entity.Performances;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.result.Result;
import com.shop.vo.PerformanceVo;
import com.shop.vo.TimeVo;

import java.util.List;

/**
* @author 陈增
* @description 针对表【performances】的数据库操作Mapper
* @createDate 2025-08-15 18:05:34
* @Entity com.shop.entity.Performances
*/
public interface PerformancesMapper extends BaseMapper<Performances> {

    List<PerformanceVo> getAllPerformance();

    List<PerformanceVo> getPerformanceById(Integer id);

    List<PerformanceVo> getPerformanceByEventId(Integer id);

    List<TimeVo> getTimeByEventId(Integer id);
}




