package com.shop.controller.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shop.entity.Orders;
import com.shop.entity.Ticket;
import com.shop.service.OrdersService;
import com.shop.service.TicketService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Component
public class test implements Job {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override

    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("开始执行任务");
        String key = "stock:ticket:";
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.select("create_time", "num", "ticket_id", "id", "user_id", "performance_id")
                .eq("status", 1);
        List<Orders> list = ordersService.list(ordersQueryWrapper);
        for (Orders orders : list) {
            LocalDateTime createTime = orders.getCreateTime();

            if (LocalDateTime.now().isAfter(createTime.plusMinutes(3))) {
                UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
                ordersUpdateWrapper.eq("id", orders.getId())
                        .set("status", 3)
                        .eq("status", 1);

                ordersService.update(ordersUpdateWrapper);
                Ticket ticket = ticketService.getById(orders.getTicketId());
                ticket.setStock(ticket.getStock() + orders.getNum());
                ticketService.updateById(ticket);
                stringRedisTemplate.opsForValue().increment(key + orders.getTicketId().toString(), orders.getNum());


            }
        }


    }
}

