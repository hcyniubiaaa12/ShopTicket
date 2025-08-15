package com.shop;

import com.shop.entity.City;
import com.shop.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShopApplicationTests {
    @Autowired
    private CityService cityService;

    @Test
    void contextLoads() {
        List<City> list = cityService.list();
        System.out.println( list);
    }

}
