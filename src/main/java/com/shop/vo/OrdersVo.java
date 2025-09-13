package com.shop.vo;

import com.shop.entity.Orders;
import com.shop.enums.SaleStatus;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class OrdersVo extends Orders {
    private SaleStatus saleStatus;
}
