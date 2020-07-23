package com.atguigu.orderservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.service.TPayLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-07-08
 */
@Api(description = "支付管理")
@RestController
@RequestMapping("/orderservice/paylog")
@CrossOrigin
public class TPayLogController {

    @Autowired
    private TPayLogService payLogService;

    @ApiOperation(value = "根据订单号生成微信二维码")
    @GetMapping("createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo){
        Map<String, Object> map =  payLogService.createNative(orderNo);
        return R.ok().data(map);
    }
    @ApiOperation(value = "查询支付状态")
    @GetMapping("queryOrderStatus/{orderNo}")
    public R queryOrderStatus(@PathVariable String orderNo){

        //1调用接口，查询订单状态
        Map<String, String> map =  payLogService.queryOrderStatus(orderNo);
        // 判断支付返回结果是否成功
        if (map.get("trade_state").equals("SUCCESS")){  //是否支付成功
            //3支付成功，修改订单状态，插入支付日志
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().code(25000).message("支付中");
    }

}

