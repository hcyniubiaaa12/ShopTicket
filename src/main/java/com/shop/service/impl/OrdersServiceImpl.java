package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.dto.OrderDto;
import com.shop.entity.Orders;
import com.shop.entity.Ticket;
import com.shop.entity.UserTicket;
import com.shop.enums.OrderStatus;
import com.shop.enums.TicketStatus;
import com.shop.result.Result;
import com.shop.service.*;
import com.shop.mapper.OrdersMapper;
import com.shop.userhold.UserHold;
import com.shop.utils.GenerateId;
import com.shop.utils.MqConstants;
import com.shop.utils.MultiDelayMessage;
import com.shop.vo.OrdersVo;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 陈增
 * @description 针对表【orders】的数据库操作Service实现
 * @createDate 2025-08-22 17:10:29
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>
        implements OrdersService {

    private OrdersService proxy;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserTicketService userTicketService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrdersServiceImpl.class);
    private static final DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
    private static final BlockingDeque<Orders> ORDERS_QUEUE = new LinkedBlockingDeque<>(1024 * 1024);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(20);


//    public class task implements Runnable {
//        @Override
//        public void run() {
//
//            while (true) {
//                try {
//                    Orders take = ORDERS_QUEUE.take();
//                    handleOrder(take);
//                } catch (Exception e) {
//                    log.error("订单处理异常", e);
//
//                }
//            }
//        }
//    }

    @Override
    public Result saveOrder(OrdersVo requestOrders) {
        switch (requestOrders.getSaleStatus()) {
            case NOT_OPENED:
                return Result.fail("该演出未开始售卖");

            case SALE_ENDED:
                return Result.fail("该演出已停止售卖");
        }

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
        Orders orders = new Orders();
        orders.setId(orderId);
        orders.setPrice(requestOrders.getPrice());
        orders.setTicketId(requestOrders.getTicketId());
        orders.setUserId(userId);
        orders.setPerformanceId(requestOrders.getPerformanceId());
        orders.setNum(requestOrders.getNum());
        super.save(orders);
        //额外开个线程进行删减库存
        proxy = (OrdersService) AopContext.currentProxy();
        //发送订单消息给消费者进行删减库存
        rabbitTemplate.convertAndSend(MqConstants.ORDER_EXCHANGE, MqConstants.ORDER_ROUTING_KEY, orders);

        //       ORDERS_QUEUE.add(orders);

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
        ArrayList<UserTicket> userTickets = new ArrayList<>(order.getNum());
        for (int i = 0; i < order.getNum(); i++) {
            UserTicket userTicket = new UserTicket();
            userTicket.setUserId(order.getUserId());
            userTicket.setTicketId(order.getTicketId());
            userTicket.setStatus(TicketStatus.UNUSED);
            userTickets.add(userTicket);
        }
        userTicketService.saveBatch(userTickets, 100);
        return Result.success("支付成功");
    }

    @Override
    public List<OrderDto> queryOrder(Integer userId) {
        return ordersMapper.queryOrder(userId);
    }

    @Override
    @Transactional
    public Result cancel(Long id) {

        Orders order = super.getById(id);
        switch (order.getStatus()) {
            case PAID:
                return Result.fail("订单已支付");
            case CANCELED:
                return Result.fail("订单已取消");
        }


        String pendingKey = "order:pending:" + order.getUserId();
        String stockKey = "stock:ticket:" + order.getTicketId();
        stringRedisTemplate.opsForHash().increment(pendingKey, order.getPerformanceId().toString(), -order.getNum());
        stringRedisTemplate.opsForValue().increment(stockKey, order.getNum());
        UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
        ordersUpdateWrapper.set("status", 3)
                .eq("id", id)
                .eq("status", 1);
        super.update(ordersUpdateWrapper);
        UpdateWrapper<Ticket> ticketUpdateWrapper = new UpdateWrapper<>();
        ticketUpdateWrapper.eq("id", order.getTicketId())
                .setSql("stock = stock +" + order.getNum());
        ticketService.update(ticketUpdateWrapper);

        return Result.success("取消成功");
    }


    @PostConstruct
    public void init() {
        ClassPathResource resource = new ClassPathResource("com/order.lua");
        redisScript.setScriptSource(new ResourceScriptSource(resource));
        redisScript.setResultType(Long.class);
//        warmUpRedis();
//        for (int i = 0; i < 20; i++) { // 启动20个消费者
//            executorService.submit(new task());
//        }

    }

//    public void handleOrder(Orders orders) {
//        RLock lock = redissonClient.getLock("order:userId:" + orders.getUserId());
//        try {
//            boolean flag = lock.tryLock(2, TimeUnit.SECONDS);
//            if (!flag) {
//                log.error("获取锁失败");
//                return;
//            }
//            proxy.reduceStock(orders);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        } finally {
//            lock.unlock();
//        }
//
//    }

    @Transactional
    public void reduceStock(Orders orders) {
        log.info(String.valueOf(orders));
        RLock lock = redissonClient.getLock("order:userId:" + orders.getUserId());
        try {
            boolean flag = lock.tryLock(2, TimeUnit.SECONDS);
            if (!flag) {
                log.error("获取锁失败");
                return;
            }
            Integer ticketId = orders.getTicketId();
            UpdateWrapper<Ticket> ticketUpdateWrapper = new UpdateWrapper<>();
            ticketUpdateWrapper.eq("id", ticketId)
                    .setSql("stock = stock - {0}", orders.getNum())
                    .ge("stock", orders.getNum());

            ticketService.update(ticketUpdateWrapper);
            MultiDelayMessage<Long> multiDelayMessage = new MultiDelayMessage<>();
            multiDelayMessage.setDelayMessage(orders.getId(), List.of(
                    60 * 1000L    // 5分钟
//                    10 * 60 * 1000L,   // 10分钟
//                    15 * 60 * 1000L    // 15分钟
            ));
            Long delay = multiDelayMessage.removeNextDelay();
            rabbitTemplate.convertAndSend(MqConstants.CHECK_ORDER_EXCHANGE, MqConstants.CHECK_ORDER_ROUTING_KEY, multiDelayMessage, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {

                    message.getMessageProperties().setDelayLong(delay);
                    return message;
                }
            });

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }


    }

    public void warmUpRedis() {
        try {
            stringRedisTemplate.opsForValue().set("warmup", "ok");
            stringRedisTemplate.expire("warmup", Duration.ofSeconds(10));
            System.out.println("✅ Redis 连接池预热完成");
        } catch (Exception e) {
            log.error("❌ Redis 连接池预热失败", e);
        }
    }

}




