package com.litserver.global.config;

import com.litserver.global.filter.AllowOriginFilter;
import com.litserver.global.filter.XSSRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<XSSRequestFilter> xssRequestFilterFilterRegistrationBean() {
        FilterRegistrationBean<XSSRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setName("xssRequestFilter");
        registration.setFilter(new XSSRequestFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<AllowOriginFilter> allowOriginFilterFilterRegistrationBean() {
        FilterRegistrationBean<AllowOriginFilter> registration = new FilterRegistrationBean<>();
        registration.setName("AllowOriginFilter");
        registration.setFilter(new AllowOriginFilter());
        registration.addUrlPatterns("/*");
        return registration;
    }
}
