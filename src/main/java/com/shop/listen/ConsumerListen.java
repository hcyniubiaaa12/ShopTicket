package com.shop.listen;

import com.shop.entity.Orders;
import com.shop.enums.OrderStatus;
import com.shop.service.OrdersService;
import com.shop.utils.Idempotent;
import com.shop.utils.MqConstants;
import com.shop.utils.MultiDelayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ConsumerListen {
    private final OrdersService ordersService;
    private final RabbitTemplate rabbitTemplate;

    public ConsumerListen(OrdersService ordersService, RabbitTemplate rabbitTemplate, StringRedisTemplate stringRedisTemplate) {
        this.ordersService = ordersService;
        this.rabbitTemplate = rabbitTemplate;
    }

    private static final Logger log = LoggerFactory.getLogger(ConsumerListen.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstants.ORDER_QUEUE),
            exchange = @Exchange(value = MqConstants.ORDER_EXCHANGE),
            key = MqConstants.ORDER_ROUTING_KEY

    ))

    public void listenReduceStock(Orders order) {

        ordersService.reduceStock(order);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstants.CHECK_ORDER_QUEUE),
            exchange = @Exchange(value = MqConstants.CHECK_ORDER_EXCHANGE, delayed = "true"),
            key = MqConstants.CHECK_ORDER_ROUTING_KEY

    ))
    public void checkOrder(MultiDelayMessage<Long> msg) {
        log.info("订单id：{}", msg.getData());
        Orders orders = ordersService.getById(msg.getData());
        if (orders == null) {
            log.error("订单不存在");
            return;
        }
        if (orders.getStatus() != OrderStatus.PENDING) {
            log.error("订单状态不为待支付");
            return;
        }
        //判断是否还有延迟时间
        if (msg.HasNextDelayTime()) {
            Long delay = msg.removeNextDelay();
            //有就重发消息
            rabbitTemplate.convertAndSend(MqConstants.CHECK_ORDER_EXCHANGE, MqConstants.CHECK_ORDER_ROUTING_KEY, msg, new MessagePostProcessor() {
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    message.getMessageProperties().setDelayLong(delay);
                    return message;
                }
            });
            return;
        }
        //没有延迟时间直接取消订单
        ordersService.cancel(msg.getData());
        log.info("订单取消成功");


    }


    @RabbitListener(queues = "dlxQueue")
    public void dlx(String message) {
        log.info("死信队列监听到消息：{}", message);
    }

}
