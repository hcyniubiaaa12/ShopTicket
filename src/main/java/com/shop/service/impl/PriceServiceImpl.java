package com.shop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shop.entity.Price;
import com.shop.service.PriceService;
import com.shop.mapper.PriceMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈增
* @description 针对表【price】的数据库操作Service实现
* @createDate 2025-08-18 17:33:48
*/
@Service
public class PriceServiceImpl extends ServiceImpl<PriceMapper, Price>
    implements PriceService{

}




