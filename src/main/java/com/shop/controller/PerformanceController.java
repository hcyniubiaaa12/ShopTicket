package com.shop.controller;

import cn.hutool.json.JSONUtil;
import com.shop.result.Result;
import com.shop.service.PerformancesService;
import com.shop.dto.PerformanceDto;
import com.shop.dto.TimeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
@RequestMapping("/performance")
public class PerformanceController {
    @Autowired
    private PerformancesService performancesService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/{id}")
    public Result<List<PerformanceDto>> getPerformanceByTypeId(@PathVariable Integer id) {
        String performance = redisTemplate.opsForValue().get("performance:type:"+id);
        if (performance == null) {
            List<PerformanceDto> list = performancesService.getPerformanceById(id);
            if (list == null) {
                redisTemplate.opsForValue().set("performance:type:"+id, " ",3,TimeUnit.SECONDS);
                return Result.success(null);
            }
            redisTemplate.opsForValue().set("performance:type:"+id, JSONUtil.toJsonStr(list));
        }
        List<PerformanceDto> list = performancesService.getPerformanceById(id);
        return Result.success(list);
    }

    @GetMapping
    public Result<List<PerformanceDto>> getAllPerformance() {
        String performance = redisTemplate.opsForValue().get("performance");
        if (performance == null) {
            List<PerformanceDto> list = performancesService.getAllPerformance();
            if (list == null) {
                redisTemplate.opsForValue().set("performance", " ");
                return Result.success(null);
            }
            redisTemplate.opsForValue().set("performance", JSONUtil.toJsonStr(list));
            return Result.success(list);
        }
        List<PerformanceDto> list = JSONUtil.toList(performance, PerformanceDto.class);
        return Result.success(list);
    }

    @GetMapping("/getPerformanceByEventId/{id}")
    public Result<List<PerformanceDto>> getPerformanceByEventId(@PathVariable Integer id) {
        List<PerformanceDto> list = performancesService.getPerformanceByEventId(id);
        return Result.success(list);
    }

    @GetMapping("/time/{id}")
    public Result<List<TimeDto>> getTimeByEventId(@PathVariable Integer id) {
        String showTime = redisTemplate.opsForValue().get("time:" + id);
        if (showTime == null) {
            List<TimeDto> list = performancesService.getTimeByEventId(id);
            if (list == null) {
                redisTemplate.opsForValue().set("time:" + id, " ");
                return Result.success(null);
            }
            redisTemplate.opsForValue().set("time:" + id, JSONUtil.toJsonStr(list));
            return Result.success(list);
        }
        List<TimeDto> list = JSONUtil.toList(showTime, TimeDto.class);
        redisTemplate.opsForValue().set("time:" + id, JSONUtil.toJsonStr(list));

        return Result.success(list);
    }
}
