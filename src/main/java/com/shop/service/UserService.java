package com.shop.service;

import com.shop.dto.PerformanceDto;
import com.shop.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.result.Result;
import com.shop.vo.CaptchaVo;

import java.util.List;

/**
* @author 陈增
* @description 针对表【user】的数据库操作Service
* @createDate 2025-08-21 17:07:14
*/
public interface UserService extends IService<User> {
    Result login(CaptchaVo captchaVo);

    boolean like(Integer userId, Integer eventId, Integer cityId);


    boolean getIsLike(Integer userId, Integer eventId, Integer cityId);

    List<PerformanceDto> getUserLike(Integer userId);
}
