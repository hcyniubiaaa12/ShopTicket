package com.shop;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shop.controller.CaptchaController;
import com.shop.controller.UploadController;
import com.shop.entity.User;
import com.shop.result.Result;
import com.shop.service.PerformancesService;
import com.shop.service.UserService;
import com.shop.service.impl.UserServiceImpl;
import com.shop.utils.GenerateId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.shop.service.impl.UserServiceImpl.generateRandomName;

@SpringBootTest
class ShopApplicationTests {
    @Autowired
    private PerformancesService performancesService;
    @Autowired
    private UploadController uploadController;
    @Autowired
    private UserServiceImpl userService;
   @Autowired
   private StringRedisTemplate stringRedisTemplate;


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
    void test2(){
       for (int i = 1; i <=10000; i++) {
           stringRedisTemplate.delete("order:pending:" + i);

       }

    }

    @Test
    void test3() {
        userService.generateTestTokens(5000);

    }

    @Test
    void test4() {
        LocalDateTime now = LocalDateTime.now();
        System.out.println( now);
    }



}
