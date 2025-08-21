package com.shop.service;

import com.shop.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.result.Result;
import com.shop.vo.CaptchaVo;

/**
* @author 陈增
* @description 针对表【user】的数据库操作Service
* @createDate 2025-08-21 17:07:14
*/
public interface UserService extends IService<User> {
    Result login(CaptchaVo captchaVo);
}
