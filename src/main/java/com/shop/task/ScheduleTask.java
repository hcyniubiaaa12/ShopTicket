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

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduleTask {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private TicketService ticketService;

    @Scheduled(cron = "0/30 * * * * ?")
    public void task() {
        LocalDateTime start = LocalDateTime.of(2025, 8, 25, 18, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 25, 19, 22, 0);
        if (LocalDateTime.now().isBefore(start) || LocalDateTime.now().isAfter(end)) {
            return;

        }
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.select("create_time", "num", "ticket_id", "id")
                .eq("status", 1);
        List<Orders> list = ordersService.list(ordersQueryWrapper);
        list.forEach(order -> {
            if (order.getCreateTime().plusMinutes(30).isBefore(LocalDateTime.now())) {
                UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
                ordersUpdateWrapper.set("status", 3);
                ordersUpdateWrapper.eq("status", 1);
                ordersUpdateWrapper.eq("id", order.getId());
                boolean update = ordersService.update(ordersUpdateWrapper);
                System.out.println(update);
                if (update) {
                    UpdateWrapper<Ticket> ticketUpdateWrapper = new UpdateWrapper<>();
                    ticketUpdateWrapper.eq("id", order.getTicketId());
                    ticketUpdateWrapper.setSql("stock = stock + " + order.getNum());
                    ticketService.update(ticketUpdateWrapper);

                }
            }
        });

    }
}
