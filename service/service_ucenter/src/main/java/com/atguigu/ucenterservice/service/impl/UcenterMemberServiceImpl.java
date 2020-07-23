package com.atguigu.ucenterservice.service.impl;

import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.ucenterservice.entity.UcenterMember;
import com.atguigu.ucenterservice.entity.vo.LoginVo;
import com.atguigu.ucenterservice.entity.vo.RegisterVo;
import com.atguigu.ucenterservice.mapper.UcenterMemberMapper;
import com.atguigu.ucenterservice.service.UcenterMemberService;
import com.atguigu.commonutils.utils.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-07-04
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UcenterMemberService ucenterService;
    @Override
    public void register(RegisterVo registerVo) {
        //1获取注册信息
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();

        //2验证参数是否为空
        if (StringUtils.isEmpty(nickname)|| StringUtils.isEmpty(mobile)|| StringUtils.isEmpty(password)|| StringUtils.isEmpty(code)){
            throw new GuliException(20001,"注册信息失败");
        }
        //3根据手机号查询是否手机号相同
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile", mobile);
        Integer count = baseMapper.selectCount(wrapper);
        if (count > 0 ){
            throw new GuliException(20001, "手机号已经注册");
        }

        //4判断验证码

        String codeRedis = redisTemplate.opsForValue().get(mobile);

        if (!code.equals(codeRedis)){
            throw new GuliException(20001, "验证码错误");
        }
        //5存入数据库
        //5.1复制数据
        UcenterMember ucenterMember = new UcenterMember();
        BeanUtils.copyProperties(registerVo, ucenterMember);

        //5.2密码加密
        String pwBefore = ucenterMember.getPassword();
        String pwAfter = MD5.encrypt(pwBefore);
        ucenterMember.setPassword(pwAfter);
        //5.3手动设置
        ucenterMember.setIsDisabled(false);
        ucenterMember.setAvatar("https://guli-file-190513.oss-cn-beijing.aliyuncs.com/avatar/default.jpg");
        //5.4存入数据
        int insert = baseMapper.insert(ucenterMember);
        if(insert==0){
            throw new GuliException(20001,"注册失败");
        }
    }

    //登录
    @Override
    public String login(LoginVo loginVo) {
        //1 获得参数
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        //2 判断参数是否为空
        if(StringUtils.isEmpty(mobile)||
                StringUtils.isEmpty(password)){
            throw  new GuliException(20001,"手机号密码有误");
        }
        //3 根据手机号查询
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember ucenterMember = baseMapper.selectOne(wrapper);
        if(ucenterMember==null){
            throw  new GuliException(20001,"手机号密码有误");
        }
        //4 验证密码
        String passwordDatabase = ucenterMember.getPassword();
        //4.1对输入密码进行MD5加密
        String encrypt = MD5.encrypt(password);
        //4.2验证加密后的密码是否一致
        if(!encrypt.equals(passwordDatabase)){
            throw  new GuliException(20001,"手机号密码有误");
        }
        //5 判断用户是否被禁用
        if(ucenterMember.getIsDisabled()){
            throw  new GuliException(20001,"手机号密码有误");
        }

        //6 生成token
        String jwtToken = JwtUtils.getJwtToken(ucenterMember.getId(), ucenterMember.getNickname());


        return jwtToken;
    }

    @Override
    public Integer countRegister(String day) {

        Integer  count = baseMapper.countRegister(day);
        return count;
    }


}
