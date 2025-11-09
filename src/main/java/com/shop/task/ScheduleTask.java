package com.shop.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shop.entity.Orders;
import com.shop.entity.Performances;
import com.shop.entity.Ticket;
import com.shop.service.OrdersService;
import com.shop.service.PerformancesService;
import com.shop.service.TicketService;
import com.shop.service.impl.OrdersServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    @Autowired
    private PerformancesService performancesService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger log = LoggerFactory.getLogger(ScheduleTask.class);


//  @Scheduled(cron = "30 * * * * ?")
//    public void checkPendingTask() {
//        String key = "stock:ticket:";
//        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
//        ordersQueryWrapper.select("create_time", "num", "ticket_id", "id", "user_id", "performance_id")
//                .eq("status", 1);
//        List<Orders> list = ordersService.list(ordersQueryWrapper);
//        for (Orders orders : list) {
//            LocalDateTime createTime = orders.getCreateTime();
//
//            if (LocalDateTime.now().isAfter(createTime.plusMinutes(2))) {
//                UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
//                ordersUpdateWrapper.eq("id", orders.getId())
//                        .set("status", 3);
//                ordersService.update(ordersUpdateWrapper);
//                Ticket ticket = ticketService.getById(orders.getTicketId());
//                ticket.setStock(ticket.getStock() + orders.getNum());
//                ticketService.updateById(ticket);
//                stringRedisTemplate.opsForValue().increment(key + orders.getTicketId().toString(), orders.getNum());
//
//
//
//
//            }
//        }
//    }


   @Scheduled(cron = " * * * * 1 ?")
   public void checkPerformanceStatus() {
       log.info("【定时任务】开始检查演出状态，当前时间: {}", LocalDateTime.now());
       QueryWrapper<Performances> queryWrapper = new QueryWrapper<>();
       queryWrapper
                   .select("buy_time","id","start_time");
       List<Performances> list = performancesService.list(queryWrapper);
       for (Performances  performances: list) {
           if(LocalDateTime.now().isAfter(performances.getBuyTime()) && LocalDateTime.now().isBefore(performances.getStartTime())){
               UpdateWrapper<Performances> updateWrapper = new UpdateWrapper<>();
               updateWrapper.eq("id", performances.getId())
                       .set("status", 2);
               performancesService.update(updateWrapper);
           }
           else if(LocalDateTime.now().isAfter(performances.getStartTime())){
               UpdateWrapper<Performances> updateWrapper = new UpdateWrapper<>();
               updateWrapper.eq("id", performances.getId())
                       .set("status", 3);
               performancesService.update(updateWrapper);
           }
//
//
//
       }
   }


}
