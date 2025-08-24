package com.shop.controller;

import com.shop.entity.Orders;
import com.shop.result.Result;
import com.shop.service.OrderService;
import com.shop.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;
    @PostMapping("/saveOrder")
    public Result saveOrder(@RequestBody Orders order){
        return ordersService.saveOrder(order);
    }
}
