package com.atguigu.wxservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.atguigu"})
@MapperScan("com.atguigu.wxservice.mapper")
public class ServiceWxApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceWxApplication.class, args);
    }
}
