package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName orders
 */
@TableName(value ="orders")
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

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Integer isDeleted;
}