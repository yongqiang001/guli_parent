package com.atguigu.orderservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.orderservice.entity.TOrder;
import com.atguigu.orderservice.entity.TPayLog;
import com.atguigu.orderservice.mapper.TPayLogMapper;
import com.atguigu.orderservice.service.TOrderService;
import com.atguigu.orderservice.service.TPayLogService;
import com.atguigu.orderservice.utils.HttpClient;
import com.atguigu.servicebase.handler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-07-08
 */
@Service
public class TPayLogServiceImpl extends ServiceImpl<TPayLogMapper, TPayLog> implements TPayLogService {

    @Autowired
    private TOrderService orderService;
    // 根据订单号生成微信二维码
    @Override
    public Map<String, Object> createNative(String orderNo) {

        try {
            // 1.根据订单号获取订单信息
            QueryWrapper<TOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            TOrder order = orderService.getOne(wrapper);
            if (order==null){
                throw new GuliException(20001, "订单失效");
            }
            // 2.封装参数：用map
            Map m = new HashMap();
            //1、设置支付参数
            m.put("appid", "wx74862e0dfcf69954");//微信支付id
            m.put("mch_id", "1558950191");//商户号
            m.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
            m.put("body", order.getCourseTitle());//商品描述
            m.put("out_trade_no", orderNo);//订单号
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");//支付金额
            m.put("spbill_create_ip", "127.0.0.1");//终端ip地址
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");//支付回调地址
            m.put("trade_type", "NATIVE");//交易类型

            // 3.创建httpClient对象
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");

            // 4.向httpClient设置xml参数
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();
            // 5.发送给请求获取返回结果
            String content = client.getContent();//xml格式
            System.out.println("content" + content);
            Map<String, String> map = WXPayUtil.xmlToMap(content);


            // 6.获取需要的值。进行封装map
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("out_trade_no", orderNo);
            resultMap.put("course_id", order.getCourseId());
            resultMap.put("total_fee", order.getTotalFee());
            resultMap.put("result_code", map.get("result_code"));
            resultMap.put("code_url", map.get("code_url"));

            return resultMap;


        } catch (Exception e) {
           throw new GuliException(20001, "生成支付二维码失败");
        }




    }

    // 调用接口，查询订单状态
    @Override
    public Map<String, String> queryOrderStatus(String orderNo) {

        try {
            // 1.封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            // 2.设置请求，发送请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();
            // 3.获取微信反馈结果xml
            String content = client.getContent();
            System.out.println("支付结果返回="+content);
            // 4.转化xml成map
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            return map;

        } catch (Exception e) {
            throw new GuliException(20001, "支付失败");
        }
    }

    // 支付成功，修改订单状态，插入支付日志
    @Override
    public void updateOrderStatus(Map<String, String> map) {
        //1修改订单状态status改为1
        //1.1根据订单号查询订单
        QueryWrapper<TOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", map.get("out_trade_no"));
        TOrder order = orderService.getOne(wrapper);
        //1.2判断订单
        if (order==null){
            throw new GuliException(20001, "订单失效");
        }
        //1.3更改状态
        order.setStatus(1);  // 1表示已支付
        orderService.updateById(order);

        //2支付日志表插入日志
        TPayLog payLog = new TPayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));
        payLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(payLog);//插入到支付日志表

    }
}
