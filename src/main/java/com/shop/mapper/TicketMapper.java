package com.shop.mapper;

import com.shop.entity.Ticket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shop.dto.TicketDto;

import java.util.List;

/**
* @author 陈增
* @description 针对表【ticket】的数据库操作Mapper
* @createDate 2025-08-19 15:55:30
* @Entity com.shop.entity.Ticket
*/
public interface TicketMapper extends BaseMapper<Ticket> {

    List<TicketDto> getAllTicketById(Integer id);
}




