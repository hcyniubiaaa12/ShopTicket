package com.shop.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shop.entity.Orders;
import com.shop.entity.Ticket;
import com.shop.service.OrdersService;
import com.shop.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleTask {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private TicketService ticketService;

    @Scheduled(cron = "0/30 * * * * ?")
    @Transactional
    public void task() {
        LocalDateTime start = LocalDateTime.of(2025, 8, 26, 13, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 26, 20, 22, 0);
        if (LocalDateTime.now().isBefore(start) || LocalDateTime.now().isAfter(end)) {
            return;

        }
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.select("create_time", "num", "ticket_id", "id")
                .eq("status", 1);
        List<Orders> list = ordersService.list(ordersQueryWrapper);
        for (Orders orders : list) {
            LocalDateTime createTime = orders.getCreateTime();
            if (LocalDateTime.now().isAfter(createTime.plusMinutes(30))) {
                UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
                ordersUpdateWrapper.eq("id", orders.getId())
                        .set("status", 3);
                ordersService.update(ordersUpdateWrapper);
                Ticket ticket = ticketService.getById(orders.getTicketId());
                ticket.setStock(ticket.getStock() + orders.getNum());
                ticketService.updateById(ticket);


            }

        }


    }
}
