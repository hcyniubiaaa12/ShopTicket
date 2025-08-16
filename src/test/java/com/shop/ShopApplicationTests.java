package com.shop;

import com.shop.controller.UploadController;
import com.shop.service.PerformancesService;
import com.shop.vo.PerformanceVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@SpringBootTest
class ShopApplicationTests {
    @Autowired
    private PerformancesService performancesService;

    @Test
    void contextLoads() throws IOException {



    }


}
