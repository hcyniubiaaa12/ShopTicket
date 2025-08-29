package com.shop.controller;

import com.shop.dto.UserTicketDto;
import com.shop.entity.UserTicket;
import com.shop.result.Result;
import com.shop.service.UserTicketService;
import com.shop.userhold.UserHold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userTicket")
@CrossOrigin
public class UserTicketController {
    @Autowired
    private UserTicketService userTicketService;
    @GetMapping("/list")
    public Result<List<UserTicketDto>>getUserTicket(){
        Integer userId = UserHold.getUser();
        List<UserTicketDto> list = userTicketService.getUserTicket(userId);
        return Result.success(list);
    }
    @PostMapping("useTicket")
    public Result useTicket(@RequestBody UserTicketDto ticket){
        return userTicketService.useTicket(ticket);

    }

}
