package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.EventType;
import com.shop.service.EventTypeService;
import com.shop.mapper.EventTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈增
* @description 针对表【event_type】的数据库操作Service实现
* @createDate 2025-08-15 18:05:17
*/
@Service
public class EventTypeServiceImpl extends ServiceImpl<EventTypeMapper, EventType>
    implements EventTypeService{

}




