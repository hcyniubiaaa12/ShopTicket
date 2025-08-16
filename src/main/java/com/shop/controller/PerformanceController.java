package com.shop.controller;

import com.shop.result.Result;
import com.shop.service.PerformancesService;
import com.shop.vo.PerformanceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/performance")
public class PerformanceController {
    @Autowired
    private PerformancesService performancesService;

    @GetMapping("/{id}")
    public Result<List<PerformanceVo>> getPerformanceById(@PathVariable Integer id ) {
        List<PerformanceVo> list = performancesService.getPerformanceById(id);
        return Result.success(list);
    }
    @GetMapping
    public Result<List<PerformanceVo>> getAllPerformance() {
        List<PerformanceVo> list = performancesService.getAllPerformance();
        return Result.success(list);
    }
    @GetMapping("/getPerformanceByEventId/{id}")
    public Result<List<PerformanceVo>> getPerformanceByEventId(@PathVariable Integer id) {
        List<PerformanceVo> list = performancesService.getPerformanceByEventId(id);
        //时间集合
        return Result.success(list);
    }
}
