package com.shop.controller;

import com.shop.dto.PerformanceDto;
import com.shop.entity.User;
import com.shop.result.Result;
import com.shop.service.UserService;
import com.shop.userhold.UserHold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/getUser")
    public Result<User> getUser(){
        User user = userService.getById(UserHold.getUser());
        return Result.success(user);


    }
    @PostMapping("/like")
    public Result like(Integer eventId, Integer cityId){
        Integer userId = UserHold.getUser();
        boolean isLike = userService.like(userId, eventId, cityId);

        return Result.success(isLike);
    }
    @GetMapping("/getIsLiked")
    public Result<Boolean> getIsLike(Integer eventId, Integer cityId){
        Integer userId = UserHold.getUser();
       boolean isLiked =  userService.getIsLike(userId,eventId,cityId);
        return Result.success(isLiked);
    }
    @GetMapping("/getUserLike")
    public Result<List<PerformanceDto>> getUserLike(){
        Integer userId = UserHold.getUser();
        List<PerformanceDto> list = userService.getUserLike(userId);
        return Result.success(list);
    }

}
