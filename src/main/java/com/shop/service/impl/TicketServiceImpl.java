package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.Ticket;
import com.shop.service.TicketService;
import com.shop.mapper.TicketMapper;
import com.shop.dto.TicketDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 陈增
* @description 针对表【ticket】的数据库操作Service实现
* @createDate 2025-08-19 15:55:30
*/
@Service
public class TicketServiceImpl extends ServiceImpl<TicketMapper, Ticket>
    implements TicketService{
    @Autowired
    private TicketMapper ticketMapper;

    @Override
    public List<TicketDto> getAllTicketById(Integer id) {
        return ticketMapper.getAllTicketById( id);
    }
}




