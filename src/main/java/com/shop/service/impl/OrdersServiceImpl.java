package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.dto.OrderDto;
import com.shop.entity.Orders;
import com.shop.entity.Ticket;
import com.shop.enums.OrderStatus;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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
    @Autowired
    private OrdersMapper ordersMapper;

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
                    requestOrders.getNum().toString(),
                    requestOrders.getPerformanceId().toString()).intValue();
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
        System.out.println("订单生成成功，订单id为：" + orderId);
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

        return Result.success(String.valueOf(orderId));
    }

    @Override
    @Transactional
    public Result pay(Long id) {
        Orders order = super.getById(id);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (!order.getUserId().equals(UserHold.getUser())) {
            return Result.fail("你没有权限支付此订单");
        }
        switch (order.getStatus()) {
            case PAID:
                return Result.fail("订单已支付");
            case CANCELED:
                return Result.fail("订单已取消");
        }
        UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
        ordersUpdateWrapper.set("status", OrderStatus.PAID)
                .eq("id", id)
                .eq("status", OrderStatus.PENDING);

        super.update(ordersUpdateWrapper);
        // 待支付订单：按 用户 + 演出场次 统计
        String pendingKey = "order:pending:" + order.getUserId();

        // 已支付订单
        String payedKey = "order:pay:" + order.getUserId();
        stringRedisTemplate.opsForHash().increment(payedKey, order.getPerformanceId().toString(), order.getNum());
        stringRedisTemplate.opsForHash().increment(pendingKey, order.getPerformanceId().toString(), -order.getNum());
        return Result.success("支付成功");
    }

    @Override
    public List<OrderDto> queryOrder(Integer userId) {
        return ordersMapper.queryOrder(userId);
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
            ticketUpdateWrapper.setSql("stock = stock - " + orders.getNum());
            ticketUpdateWrapper.ge("stock", orders.getNum());
            ticketService.update(ticketUpdateWrapper);


        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }


    }

}




