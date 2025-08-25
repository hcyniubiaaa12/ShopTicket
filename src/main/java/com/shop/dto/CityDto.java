package com.shop.dto;

import com.shop.entity.City;
import lombok.Data;

@Data
public class CityDto extends City {
    private String venue;
}
