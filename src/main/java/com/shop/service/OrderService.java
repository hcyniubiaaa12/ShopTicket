package com.shop.service;

import com.shop.entity.Orders;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.result.Result;

/**
* @author 陈增
* @description 针对表【order】的数据库操作Service
* @createDate 2025-08-22 14:33:55
*/
public interface OrderService extends IService<Orders> {

    Result saveOrder(Orders requestOrders);


}
