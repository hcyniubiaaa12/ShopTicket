package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.Events;
import com.shop.service.EventsService;
import com.shop.mapper.EventsMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈增
* @description 针对表【events】的数据库操作Service实现
* @createDate 2025-08-15 18:05:22
*/
@Service
public class EventsServiceImpl extends ServiceImpl<EventsMapper, Events>
    implements EventsService{

}




