package com.shop.vo;

import com.shop.entity.Orders;
import com.shop.enums.SaleStatus;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@ToString(callSuper = true)
public class OrdersVo extends Orders {
    private SaleStatus saleStatus;
}
