package com.shop;

import com.shop.controller.UploadController;
import com.shop.result.Result;
import com.shop.service.OrdersService;
import com.shop.service.PerformancesService;
import com.shop.service.TicketService;
import com.shop.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
       stringRedisTemplate.opsForValue().set("stock:ticket:"+54, "10000");
       stringRedisTemplate.opsForValue().set("stock:ticket:"+55, "10000");
       stringRedisTemplate.opsForValue().set("stock:ticket:"+56, "10000");

    }

    @Test
    void test3() {
        userService.generateTestTokens(1500);

    }

    @Test

    void test4() throws InterruptedException {


    }


}
