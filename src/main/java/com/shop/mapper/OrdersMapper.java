package com.shop.mapper;

import com.shop.dto.OrderDto;
import com.shop.entity.Orders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author 陈增
* @description 针对表【orders】的数据库操作Mapper
* @createDate 2025-08-22 17:10:29
* @Entity com.shop.entity.Orders
*/
public interface OrdersMapper extends BaseMapper<Orders> {

    List<OrderDto> queryOrder(Integer userId);
}




