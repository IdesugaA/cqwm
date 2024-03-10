package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.BaseException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description UserServiceImpl
 * @Author songyu
 * @Date 2023-09-27
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final WeChatProperties weChatProperties;

    /**
     * 用户登录的方法
     *
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //1.远程调用微信认证接口获取openid
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",userLoginDTO.getCode());
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(url, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = (String) jsonObject.get("openid");
        if(openid==null){
            throw new BaseException(MessageConstant.LOGIN_FAILED);
        }
        //2.根据openid查询用户对象
        User user = userMapper.findByOpenId(openid);
        //2.1 没有找到对应用户对象，将openid注册到用户表中（新用户注册）
        if(user == null){
            user = new User();
            user.setOpenid(openid);
            user.setCreateTime(LocalDateTime.now());
            userMapper.insert(user);
        }
        //3.直接返回用户对象
        return user;
    }
}
