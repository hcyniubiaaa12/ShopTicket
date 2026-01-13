package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.entity.Orders;
import com.shop.result.Result;
import com.shop.service.OrdersService;
import com.shop.userhold.UserHold;
import com.shop.vo.OrdersVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private OrdersService ordersService;
    private int count = 0;

    @PostMapping("/saveOrder")

    public Result saveOrder(@RequestBody OrdersVo order) {

        return ordersService.saveOrder(order);
    }

    @PostMapping("pay/{id}")

    public Result pay(@PathVariable Long id) {

        return ordersService.pay(id);
    }

    @GetMapping("/list")
    public Result<List<OrderDto>>  queryOrder(){
        Integer userId = UserHold.getUser();
        List<OrderDto> list = ordersService.queryOrder(userId);
        return Result.success( list);
    }

    @PostMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id){
        return ordersService.cancel(id);
    }

}
