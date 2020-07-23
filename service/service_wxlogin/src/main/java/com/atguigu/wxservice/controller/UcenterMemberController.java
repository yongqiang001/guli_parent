package com.atguigu.wxservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.wxservice.entity.UcenterMember;
import com.atguigu.wxservice.service.UcenterMemberService;
import com.atguigu.wxservice.util.ConstantPropertiesUtil;
import com.atguigu.wxservice.util.HttpClientUtils;
import com.google.gson.Gson;
import com.sun.deploy.security.CertStore;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-07-05
 */
@Api(description = "微信扫码登录")
@Controller   //重定向
@RequestMapping("/api/ucenter/wx")
@CrossOrigin
public class UcenterMemberController {
    @Autowired
    private UcenterMemberService memberService;

    @ApiOperation(value = "生成微信登录二维码")
    @GetMapping("login")
    public String login(){
        //方案一：拼接字符串
        //https://open.weixin.qq.com/connect/qrconnect?
        // appid=APPID&redirect_uri=REDIRECT_URI&
        // response_type=code&scope=SCOPE&state=STATE#wechat_redirect
        //方案二：
        //搭建开发平台授权url
        //url中参数使用占位符%s，传递需要的参数
        // 微信开放平台授权baseUrl
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";
        //回调地址
        String wxOpenRedirectUrl = ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL;
        try {
            wxOpenRedirectUrl = URLEncoder.encode(wxOpenRedirectUrl,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //传递参数
        String wxUrl = String.format(
                baseUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                wxOpenRedirectUrl,
                "wxatguigu"
        );
        //重定向
        return "redirect:" + wxUrl;
    }

    @ApiOperation(value = "微信扫描后的回调方法")
    @GetMapping("callback")
    public String callback(String code, String state){
        System.out.println("code : " + code);
        System.out.println("state : " + state);
        //1拿到参数code，请求微信固定地址，获得参数openid、access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        baseAccessTokenUrl= String.format(
                baseAccessTokenUrl,
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code
        );


        try {
            String accessTokenInfo = HttpClientUtils.get(baseAccessTokenUrl);
            System.out.println("accessTokenInfo = "+accessTokenInfo);
            Gson gson = new Gson();
            HashMap<String,Object> accessTokenMap =  gson.fromJson(accessTokenInfo, HashMap.class);
            //从map获取相关参数
            String openid = (String)accessTokenMap.get("openid");
            String access_token = (String)accessTokenMap.get("access_token");
            //拿两个参数请求微信固定地址，获取微信用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            baseUserInfoUrl= String.format(
                    baseUserInfoUrl,
                    access_token,
                    openid
            );
            //httpclient请求
            String userInfo = HttpClientUtils.get(baseUserInfoUrl);
            System.out.println("userInfo ="+userInfo);
            HashMap<String,Object> userInfoMap = gson.fromJson(userInfo,HashMap.class);
            String nickname = (String)userInfoMap.get("nickname");
            String headimgurl = (String)userInfoMap.get("headimgurl");

            //根据openid查询用户
            UcenterMember member = memberService.getWxInfoByOpenid(openid);
            if(member==null){
                member = new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                memberService.save(member);
            }
            //生成token，并返回
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
            //返回首页
            return "redirect:http://localhost:3000?token="+jwtToken;


        } catch (Exception e) {
            throw new GuliException(20001,"扫描失败");
        }

    }


}

