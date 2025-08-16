package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName ticket
 */
@TableName(value ="ticket")
@Data
public class Ticket {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer performanceId;

    /**
     * 
     */
    private Integer ticketTypeId;

    /**
     * 
     */
    private Integer stock;

    /**
     * 
     */
    private Integer status;

    /**
     * 
     */
    private Integer isDeleted;
}