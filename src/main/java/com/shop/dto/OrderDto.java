package com.shop.dto;

import com.shop.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDto {
    private String id;
    private String title;
    private String city;
    private Integer num;
    private Integer price;
    private String ticketType;
    private OrderStatus status;
    private String venue;
    private String url;
    private Integer duration;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
}
