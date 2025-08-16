package com.shop.controller;

import cn.hutool.json.JSONUtil;
import com.shop.result.Result;
import com.shop.service.PerformancesService;
import com.shop.vo.PerformanceVo;
import com.shop.vo.TimeVo;
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
    public Result<List<PerformanceVo>> getPerformanceByTypeId(@PathVariable Integer id) {
        String performance = redisTemplate.opsForValue().get("performance:type:"+id);
        if (performance == null) {
            List<PerformanceVo> list = performancesService.getPerformanceById(id);
            if (list == null) {
                redisTemplate.opsForValue().set("performance:type:"+id, " ",3,TimeUnit.SECONDS);
                return Result.success(null);
            }
            redisTemplate.opsForValue().set("performance:type:"+id, JSONUtil.toJsonStr(list));
        }
        List<PerformanceVo> list = performancesService.getPerformanceById(id);
        return Result.success(list);
    }

    @GetMapping
    public Result<List<PerformanceVo>> getAllPerformance() {
        String performance = redisTemplate.opsForValue().get("performance");
        if (performance == null) {
            List<PerformanceVo> list = performancesService.getAllPerformance();
            if (list == null) {
                redisTemplate.opsForValue().set("performance", " ");
                return Result.success(null);
            }
            redisTemplate.opsForValue().set("performance", JSONUtil.toJsonStr(list));
            return Result.success(list);
        }
        List<PerformanceVo> list = JSONUtil.toList(performance, PerformanceVo.class);
        return Result.success(list);
    }

    @GetMapping("/getPerformanceByEventId/{id}")
    public Result<List<PerformanceVo>> getPerformanceByEventId(@PathVariable Integer id) {
        List<PerformanceVo> list = performancesService.getPerformanceByEventId(id);
        return Result.success(list);
    }

    @GetMapping("/time/{id}")
    public Result<List<TimeVo>> getTimeByEventId(@PathVariable Integer id) {
        String showTime = redisTemplate.opsForValue().get("time:" + id);
        if (showTime == null) {
            List<TimeVo> list = performancesService.getTimeByEventId(id);
            if (list == null) {
                redisTemplate.opsForValue().set("time:" + id, " ");
                return Result.success(null);
            }
            redisTemplate.opsForValue().set("time:" + id, JSONUtil.toJsonStr(list));
            return Result.success(list);
        }
        List<TimeVo> list = JSONUtil.toList(showTime, TimeVo.class);
        redisTemplate.opsForValue().set("time:" + id, JSONUtil.toJsonStr(list));

        return Result.success(list);
    }
}
