package com.shop.dto;

import lombok.Data;

import java.util.Date;

@Data

public class PerformanceDto {

    private Integer minPrice;
    private String description;
    private  Integer eventId;
    private String title;
    private String typeName;
    private String venueName;
    private String cityName;
    private String url;
    private Integer duration;
    private Date firstShow;
    private Date lastShow;
    private Integer availableSeats;
    private Integer totalPerformances;



}
