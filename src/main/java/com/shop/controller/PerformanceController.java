package com.shop.controller;

import cn.hutool.json.JSONUtil;
import com.shop.result.Result;
import com.shop.service.PerformancesService;
import com.shop.dto.PerformanceDto;
import com.shop.dto.TimeDto;
import com.shop.userhold.UserHold;
import com.shop.utils.RedisUtils;
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
    @Autowired
    private RedisUtils redisUtils;

    @GetMapping
    /*获取某类型的演出*/
    public Result<List<PerformanceDto>> getPerformanceByTypeId(@RequestParam(defaultValue = "") Integer typeId, @RequestParam(defaultValue = "") Integer cityId) {
//        String performance = redisTemplate.opsForValue().get("performance:type:"+id);
//        if (performance == null) {
        List<PerformanceDto> list = performancesService.getPerformanceById(typeId, cityId);
//            if (list == null) {
//                redisTemplate.opsForValue().set("performance:type:"+id, " ",3,TimeUnit.SECONDS);
//                return Result.success(null);
//            }
//            redisTemplate.opsForValue().set("performance:type:"+id, JSONUtil.toJsonStr(list));
//        }
//        List<PerformanceDto> list = performancesService.getPerformanceById(id);
        return Result.success(list);
    }


    @GetMapping("/getPerformanceByEventId/{id}")
    /*具体城市演出*/
    public Result<List<PerformanceDto>> getPerformanceByEventId(@PathVariable Integer id, Integer cityId) {
        List<PerformanceDto> list = performancesService.getPerformanceByEventId(id, cityId);
        return Result.success(list);
    }

    @GetMapping("/time/{id}")
    /*获取演出时间*/
    public Result<List<TimeDto>> getTimeByEventId(@PathVariable Integer id, Integer cityId) {
        String key = id.toString()+":"+cityId.toString();
        List<TimeDto> list = redisUtils.getWithLockList("time",key, TimeDto.class, () -> performancesService.getTimeByEventId(id, cityId));

        //   List<TimeDto> list = performancesService.getTimeByEventId(id,cityId);
//

        return Result.success(list);
    }

    @GetMapping("/getMyPerformance")
    /*获取用户已经购买的演出 */
    public Result<List<PerformanceDto>> getMyPerformance() {
        List<PerformanceDto> list = performancesService.getMyPerformance(UserHold.getUser());
        return Result.success(list);
    }
}
