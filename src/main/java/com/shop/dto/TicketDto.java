package com.shop.dto;

import lombok.Data;

@Data
public class TicketDto {
    private Integer id;
    private String name;
    private Integer price;
    private Integer stock;
}
