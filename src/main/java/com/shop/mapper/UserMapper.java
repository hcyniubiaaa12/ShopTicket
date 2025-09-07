package com.shop.mapper;

import com.shop.dto.PerformanceDto;
import com.shop.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 陈增
* @description 针对表【user】的数据库操作Mapper
* @createDate 2025-08-21 17:07:14
* @Entity com.shop.entity.User
*/
public interface UserMapper extends BaseMapper<User> {

    List<Integer> like(Integer eventId, Integer cityId);

    List<PerformanceDto> getUserLike( List<Integer> performanceIds);
}




