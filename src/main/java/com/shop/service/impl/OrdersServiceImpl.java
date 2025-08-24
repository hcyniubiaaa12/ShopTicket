package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.Orders;
import com.shop.entity.Ticket;
import com.shop.result.Result;
import com.shop.service.OrdersService;
import com.shop.mapper.OrdersMapper;
import com.shop.service.TicketService;
import com.shop.userhold.UserHold;
import com.shop.utils.GenerateId;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 陈增
 * @description 针对表【orders】的数据库操作Service实现
 * @createDate 2025-08-22 17:10:29
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RedissonClient redissonClient;

    private static final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
    private static final BlockingDeque<Orders> ORDERS_QUEUE = new LinkedBlockingDeque<>(1024);
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();


    public class task implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Orders take = ORDERS_QUEUE.take();
                    reduceStock(take);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Result saveOrder(Orders requestOrders) {
        Integer userId = UserHold.getUser();
        Integer result = null;
        try {
            result = stringRedisTemplate.execute(redisScript,
                    Collections.emptyList(),
                    userId.toString(),
                    requestOrders.getTicketId().toString(),
                    requestOrders.getNum().toString()).intValue();
        } catch (Exception e) {
            log.error("执行 Lua 脚本失败", e);
            return Result.fail("系统异常，请重试");
        }
        // ✅ 先判空
        if (result == null) {
            log.warn("Lua 脚本返回 null，可能是执行失败");
            return Result.fail("库存校验失败，请重试");
        }
        switch (result) {
            case 1:
                return Result.fail("库存不足");
            case 2:
                return Result.fail("你已购买4张");
        }

        GenerateId work = new GenerateId(stringRedisTemplate);
        long orderId = work.generateId("order");
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setPrice(requestOrders.getPrice());
        orders.setTicketId(requestOrders.getTicketId());
        orders.setUserId(userId);
        orders.setPerformanceId(requestOrders.getPerformanceId());
        orders.setNum(requestOrders.getNum());
        super.save(orders);
        //额外开个线程进行删减库存
        ORDERS_QUEUE.add(orders);

        return Result.success(orderId);
    }

    @PostConstruct
    public void init() {
        ClassPathResource resource = new ClassPathResource("com/order.lua");
        redisScript.setScriptSource(new ResourceScriptSource(resource));
        redisScript.setResultType(Long.class);
        executorService.submit(new task());
    }

    public void reduceStock(Orders orders) {
        RLock lock = redissonClient.getLock("lock:ticket:" + orders.getUserId() + ":" + orders.getTicketId());
        try {
            boolean b = lock.tryLock(10, TimeUnit.SECONDS);
            if (!b) {
                return;
            }
            Integer ticketId = orders.getTicketId();
            UpdateWrapper<Ticket> ticketUpdateWrapper = new UpdateWrapper<>();
            ticketUpdateWrapper.eq("id", ticketId);
            ticketUpdateWrapper .setSql("stock = stock - " + orders.getNum());
            ticketUpdateWrapper.ge("stock", orders.getNum());
            ticketService.update(ticketUpdateWrapper);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }


    }

}




