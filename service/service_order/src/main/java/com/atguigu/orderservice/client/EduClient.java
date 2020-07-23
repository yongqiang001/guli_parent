package com.atguigu.orderservice.client;


import com.atguigu.commonutils.vo.CourseWebVoPay;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-edu")
public interface EduClient {
    //根据id查询课程、讲师信息，支付订单模块远程调用
    @GetMapping("/eduservice/courseapi/getCourseInfoPay/{id}")
    public CourseWebVoPay getCourseInfoPay(@PathVariable("id") String id);
}