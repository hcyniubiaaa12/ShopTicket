package com.shop.controller;

import com.shop.result.Result;
import com.shop.service.TicketService;
import com.shop.dto.TicketDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/ticket")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @RequestMapping("/getTicketById/{id}")
    public Result<List<TicketDto>> getAllTicketById(@PathVariable Integer id) {
        List<TicketDto> list =  ticketService.getAllTicketById(id);
        return Result.success(list);


    }
}
