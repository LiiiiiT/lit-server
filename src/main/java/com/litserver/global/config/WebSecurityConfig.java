package com.litserver.global.config;

import com.litserver.global.filter.AllowOriginFilter;
import com.litserver.global.filter.XSSRequestFilter;
import com.litserver.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                .authorizeRequests()//
                // 권한 설정
                .antMatchers("/v2/api-docs/**", "/swagger-resources/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**", "/swagger/**",   // swagger
                        "/favicon.ico").hasIpAddress("127.0.0.1")
                .antMatchers("/users/signin").permitAll()//
                .antMatchers("/users/signup").permitAll()//
                .antMatchers("/api/health-check").permitAll()//

                // Disallow everything else..
                .anyRequest().authenticated();

        // If a user try to access a resource without having enough permissions
        httpSecurity.exceptionHandling().accessDeniedPage("/login");
        // Apply JWT
        httpSecurity.apply(new JwtTokenConfig(jwtTokenProvider));

        return httpSecurity.build();
    }
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
