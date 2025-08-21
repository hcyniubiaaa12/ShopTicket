package com.shop.controller;

import com.shop.entity.User;
import com.shop.result.Result;
import com.shop.service.UserService;
import com.shop.userhold.UserHold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
