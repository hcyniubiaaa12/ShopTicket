package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.Performances;
import com.shop.service.PerformancesService;
import com.shop.mapper.PerformancesMapper;
import com.shop.dto.PerformanceDto;
import com.shop.dto.TimeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author
* @description 针对表【performances】的数据库操作Service实现
* @createDate 2025-08-15 18:05:34
*/
@Service
public class PerformancesServiceImpl extends ServiceImpl<PerformancesMapper, Performances>
    implements PerformancesService{
    @Autowired
    private PerformancesMapper performancesMapper;

    @Override
    public List<PerformanceDto> getPerformanceById(Integer id) {
        return performancesMapper.getPerformanceById(id);
    }

    @Override
    public List<PerformanceDto> getAllPerformance() {
        return performancesMapper.getAllPerformance();
    }

    @Override
    public List<PerformanceDto> getPerformanceByEventId(Integer id, Integer cityId) {
        return  performancesMapper.getPerformanceByEventId(id,cityId);
    }

    @Override
    public List<TimeDto> getTimeByEventId(Integer id,Integer cityId) {
        return performancesMapper.getTimeByEventId(id,cityId);
    }

    @Override
    public List<PerformanceDto> getMyPerformance(Integer userId) {
        return performancesMapper.getMyPerformance(userId);
    }
}




