package com.atguigu.orderservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.orderservice.client.UcenterClient;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.service.TOrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-07-08
 */
@Api(description = "订单管理")
@RestController
@RequestMapping("/orderservice/order")
@CrossOrigin
public class TOrderController {
    @Autowired
    private TOrderService orderService;


    @ApiOperation(value = "生成课程支付订单")
    @GetMapping("createOrder/{courseId}")
    public R createOrder(@PathVariable String courseId,
                         HttpServletRequest request){
        //根据token获取用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //生成订单，返回订单号，参数：课程id、用户id
        String orderNo = orderService.createOrder(courseId,memberId);
        return R.ok().data("orderNo",orderNo);
    }


    @ApiOperation(value = "根据订单编号获取订单信息")
    @GetMapping("getOrderInfo/{orderNo}")
    public R getOrderInfo(@PathVariable String orderNo ){

        QueryWrapper<TOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        TOrder order = orderService.getOne(wrapper);
        return R.ok().data("orderInfo",  order);
    }
    @ApiOperation(value = "查询课程是否被某用户购买")
    @GetMapping("isBuyCourse/{cid}/{mid}")
    public boolean isBuyCourse(@PathVariable String cid, @PathVariable String mid){
        QueryWrapper<TOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",cid);
        wrapper.eq(("member_id"), mid);
        wrapper.eq("status", 1);
        int count = orderService.count(wrapper);
        return count > 0 ;
    }


}

