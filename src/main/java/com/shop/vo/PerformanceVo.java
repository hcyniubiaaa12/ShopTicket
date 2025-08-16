package com.shop.vo;

import com.shop.entity.Performances;
import lombok.Data;

import java.util.Date;

@Data

public class PerformanceVo {

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
