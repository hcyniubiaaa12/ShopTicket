package com.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.dto.UserTicketDto;
import com.shop.entity.UserTicket;
import com.shop.enums.TicketStatus;
import com.shop.result.Result;
import com.shop.service.UserTicketService;
import com.shop.mapper.UserTicketMapper;
import com.shop.userhold.UserHold;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 陈增
 * @description 针对表【user_ticket】的数据库操作Service实现
 * @createDate 2025-08-28 19:59:35
 */
@Service
public class UserTicketServiceImpl extends ServiceImpl<UserTicketMapper, UserTicket>
        implements UserTicketService {
    @Autowired
    private UserTicketMapper userTicketMapper;

    @Override
    public List<UserTicketDto> getUserTicket(Integer userId) {
        return userTicketMapper.getUserTicket(userId);
    }

    @Override
    public Result useTicket(UserTicketDto ticket) {
        LocalDateTime startTime = ticket.getStartTime();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime) || now.isAfter(startTime.plusMinutes(ticket.getDuration())) ) {
            return Result.fail("该票当前不能使用");
        }
        if(ticket.getStatus() == TicketStatus.USED){
            return Result.fail("该票已使用");
        }
        UpdateWrapper<UserTicket> userTicketUpdateWrapper = new UpdateWrapper<>();
        userTicketUpdateWrapper.set("status", TicketStatus.USED)
                .eq("ticket_id", ticket.getTicketId())
                .eq("user_id", UserHold.getUser())
                .eq("status", TicketStatus.UNUSED)
                .eq("id", ticket.getId());

        super.update(userTicketUpdateWrapper);
        return Result.success("使用成功");
    }
}




