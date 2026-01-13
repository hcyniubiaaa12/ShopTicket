package com.shop;

import com.shop.controller.OrderController;
import com.shop.controller.UploadController;
import com.shop.dto.PerformanceDto;
import com.shop.dto.TimeDto;
import com.shop.result.Result;
import com.shop.service.OrdersService;
import com.shop.service.PerformancesService;
import com.shop.service.TicketService;
import com.shop.service.impl.UserServiceImpl;
import com.shop.userhold.UserHold;
import com.shop.utils.Idempotent;
import com.shop.utils.RedisUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;

@SpringBootTest
class ShopApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(ShopApplicationTests.class);
    @Autowired
    private PerformancesService performancesService;
    @Autowired
    private UploadController uploadController;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisUtils redisUtils;


    @Test
    void contextLoads() throws IOException {
        ClassPathResource resource = new ClassPathResource("com/static/teh.webp");

        // 2. 读取文件内容
        byte[] bytes;
        try (var inputStream = resource.getInputStream()) {
            bytes = inputStream.readAllBytes();
        }

        // 3. 创建 MultipartFile（使用 MockMultipartFile）
        String filename = resource.getFilename(); // dzq.jpg
        String contentType = "image/jpeg"; // 可根据需要调整

        MultipartFile multipartFile = new MockMultipartFile(
                "file",           // 表单字段名（随便写，只要 controller 接受就行）
                filename,         // 原始文件名
                contentType,      // 内容类型
                bytes             // 文件字节流
        );
        Result upload = uploadController.upload(multipartFile);
        System.out.println(upload);


    }

    @Test
    void test2() {
       stringRedisTemplate.opsForValue().set("stock:ticket:"+57, "10000");
       stringRedisTemplate.opsForValue().set("stock:ticket:"+58, "10000");
       stringRedisTemplate.opsForValue().set("stock:ticket:"+59, "10000");

    }

    @Test
    void test3() {
        userService.generateTestTokens(1500);

    }

    @Test

    void test4()  {
        rabbitTemplate.convertAndSend("simpleExchange","simpleRoutingKey","hello world");



    }
    @Test
    void test5()  {
       // redisUtils.getWithLockList("time",57, TimeDto.class, () -> performancesService.getTimeByEventId(1,13))  ;

    }
    @Test
    void test6() throws Exception {
        ordersService.pay(1L);
        ordersService.pay(1L);



    }


}
