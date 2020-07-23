package com.atguigu.msmservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.atguigu.msmservice.service.MsmService;
import com.atguigu.servicebase.handler.GuliException;
import javafx.beans.DefaultProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MsmServiceImpl implements MsmService {

    //根据手机号发送验证码
    @Override
    public boolean sedMsm(String phone, Map<String, String> map) {
        //0 判断手机号
        if (StringUtils.isEmpty(phone)){
            return false;
        }
        //1 创建初始化对象
        DefaultProfile profile = DefaultProfile.getProfile("default",
                "LTAI4GJPVfxa8f8ifkhubHPn", "LxVJ72WvpmUlM7lgMHJbY824cJX3vD");
        IAcsClient client = new DefaultAcsClient(profile);
        //2 创建request对象
        CommonRequest request = new CommonRequest();
        //3 向request里设置参数
        request.setMethod(MethodType.POST);
        request.setDomain("dysmsapi.aliyuncs.com");
        request.setVersion("2017-05-25");
        request.setAction("SendSms");

        request.putQueryParameter("PhoneNumbers", phone);
        request.putQueryParameter("SignName", "谷粒在线教育网站");
        request.putQueryParameter("TemplateCode", "SMS_195225012");
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(map));


        try {
            //4 调用初始化兑现方法实现发送
            CommonResponse response =  client.getCommonResponse(request);
            //5 通过response获取发送是否成功
            boolean success = response.getHttpResponse().isSuccess();
            return success;

        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }

    }
}
