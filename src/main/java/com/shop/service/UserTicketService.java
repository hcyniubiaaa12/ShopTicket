package com.shop.service;

import com.shop.dto.UserTicketDto;
import com.shop.entity.UserTicket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.result.Result;

import java.util.List;

/**
* @author 陈增
* @description 针对表【user_ticket】的数据库操作Service
* @createDate 2025-08-28 19:59:35
*/
public interface UserTicketService extends IService<UserTicket> {

    List<UserTicketDto> getUserTicket(Integer userId);

    Result useTicket(UserTicketDto ticket);
}
