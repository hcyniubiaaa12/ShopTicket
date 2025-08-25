package com.shop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketDto {
    private Integer id;
    private String name;
    private Integer price;
    private Integer stock;
}
