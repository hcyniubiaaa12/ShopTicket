package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName performances
 */
@TableName(value ="performances")
@Data
public class Performances {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer eventId;

    /**
     * 关联场馆
     */
    private Integer venueId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 演出时长
     */
    private Integer duration;

    /**
     * 
     */
    private Integer status;

    /**
     * 座位
     */
    private Integer availableSeats;

    /**
     * 简介
     */
    private String description;

    /**
     * 
     */
    private Integer isDeleted;
}