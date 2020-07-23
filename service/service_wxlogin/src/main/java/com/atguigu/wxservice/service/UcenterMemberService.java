package com.atguigu.wxservice.service;

import com.atguigu.wxservice.entity.UcenterMember;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-07-05
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    UcenterMember getWxInfoByOpenid(String openid);
}
