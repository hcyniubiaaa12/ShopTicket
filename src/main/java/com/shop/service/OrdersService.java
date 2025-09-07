package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.result.Result;

import java.util.List;

/**
* @author 陈增
* @description 针对表【orders】的数据库操作Service
* @createDate 2025-08-22 17:10:29
*/
public interface OrdersService extends IService<Orders> {

    Result saveOrder(Orders order);


    Result pay(Long id);

    List<OrderDto> queryOrder(Integer userId);

    Result cancel(Long id);

    void reduceStock(Orders take);
}
