package com.shop.service;

import com.shop.entity.Ticket;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.vo.TicketVo;

import java.util.List;

/**
* @author 陈增
* @description 针对表【ticket】的数据库操作Service
* @createDate 2025-08-19 15:55:30
*/
public interface TicketService extends IService<Ticket> {

    List<TicketVo> getAllTicketById(Integer id);
}
