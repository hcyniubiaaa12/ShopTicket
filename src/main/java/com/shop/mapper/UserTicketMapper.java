package com.shop.mapper;

import com.shop.dto.UserTicketDto;
import com.shop.entity.UserTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 陈增
* @description 针对表【user_ticket】的数据库操作Mapper
* @createDate 2025-08-28 19:59:35
* @Entity com.shop.entity.UserTicket
*/
public interface UserTicketMapper extends BaseMapper<UserTicket> {

    List<UserTicketDto> getUserTicket(Integer userId);
}




