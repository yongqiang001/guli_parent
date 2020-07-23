package com.atguigu.eduservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Component
@FeignClient("service-order")
public interface OrderClient {

    //查询课程是否被某用户购买
    @GetMapping("/orderservice/order/isBuyCourse/{cid}/{mid}")
    public boolean isBuyCourse(@PathVariable("cid") String cid, @PathVariable("mid") String mid);
}
