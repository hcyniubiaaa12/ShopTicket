package com.shop.dto;

import com.shop.enums.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserTicketDto {
    private Integer id;
    private  Integer price;
    private Integer ticketId;
    private String username;
    private String title;
    private  String city;
    private String ticketType;
    private String venue;
    private String url;

    private TicketStatus status;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime createTime;

}
