package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;
import java.util.Date;

import com.shop.enums.OrderStatus;
import lombok.Data;

/**
 * @TableName orders
 */
@TableName(value = "orders")
@Data
public class Orders {
    /**
     *
     */
    @TableId
    private Long id;

    /**
     *
     */
    private Integer num;

    private Integer performanceId;

    /**
     *
     */
    private Integer price;

    /**
     *
     */
    private Integer userId;

    /**
     *
     */
    private Integer ticketId;
    private OrderStatus status;

    /**
     *
     */
    private LocalDateTime createTime;

    /**
     *
     */
    private Integer isDeleted;
}