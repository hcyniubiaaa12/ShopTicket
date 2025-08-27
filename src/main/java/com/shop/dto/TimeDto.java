package com.shop.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class TimeDto {
    private Integer id;
    private LocalDateTime time;
}
