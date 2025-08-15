package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 
 * @TableName events
 */
@TableName(value ="events")
@Data
public class Events {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 歌手
     */
    private String artist;

    /**
     * 演出类型 话剧 演唱会
     */
    private Integer typeId;

    /**
     * 
     */
    private Integer status;

    /**
     * 
     */
    private Integer isDeleted;
}