package com.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.shop.enums.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 
 * @TableName user_ticket
 */
@TableName(value ="user_ticket")
@Data
public class UserTicket {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

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
    private TicketStatus status;

    /**
     * 
     */
    private LocalDateTime createTime;
    private Integer isDeleted;
}