package com.shop;

import com.shop.controller.CaptchaController;
import com.shop.controller.UploadController;
import com.shop.dto.CaptchaDto;
import com.shop.result.Result;
import com.shop.service.PerformancesService;
import com.shop.vo.PerformanceVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootTest
class ShopApplicationTests {
    @Autowired
    private PerformancesService performancesService;
    @Autowired
    private UploadController uploadController;
    @Autowired
    private CaptchaController captchaController;

    @Test
    void contextLoads() throws IOException {
        ClassPathResource resource = new ClassPathResource("com/static/wlh.webp");

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
        Result<CaptchaDto> captcha = captchaController.getCaptcha();
        System.out.println(captcha);
    }


}
