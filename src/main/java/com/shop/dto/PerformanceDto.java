package com.shop.dto;

import lombok.Data;

import java.lang.ref.PhantomReference;
import java.util.Date;

@Data

public class PerformanceDto {

    private Integer minPrice;
    private  Integer eventId;
    private String title;
    private String typeName;
    private String artist;
    private String city;
    private String venue;
    private String url;
    private String description;
    private  String availableSeats;
    private String cityId;
    private Integer duration;
    private Date firstShow;
    private Date lastShow;




}
