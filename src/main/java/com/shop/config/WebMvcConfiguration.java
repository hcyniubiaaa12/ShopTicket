package com.shop.config;

import com.shop.convert.StringToBaseEnumConverterFactory;
import com.shop.interceptor.LoginInterceptor;
import com.shop.interceptor.RefreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(this.stringToBaseEnumConverterFactory);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RefreshInterceptor(stringRedisTemplate))
                .excludePathPatterns("/login",
                        "/getCaptcha"
                        );
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/login",
                        "/getCaptcha",
                        "/performance/{id}",
                        "/performance/getPerformanceByEventId/{id}",
                        "/performance",
                        "/time/{id}",
                        "/type");
    }


}