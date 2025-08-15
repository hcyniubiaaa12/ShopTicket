package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName venues
 */
@TableName(value ="venues")
@Data
public class Venues {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String name;

    /**
     * 
     */
    private Integer cityId;

    /**
     * 
     */
    private String address;

    /**
     * 
     */
    private Integer capacity;

    /**
     * 
     */
    private Integer isDeleted;
}