package com.own.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.own.constant.MessageConstant;
import com.own.dto.UserLoginDTO;
import com.own.entity.User;
import com.own.exception.BaseException;
import com.own.mapper.UserMapper;
import com.own.properties.WeChatProperties;
import com.own.service.UserService;
import com.own.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

	private final UserMapper userMapper;
	private final WeChatProperties weChatProperties;

	@Override
	public User login(UserLoginDTO userLoginDTO){

		//远程调用微信认证接口获取openid
		String url = "https://api.weixin.qq.com/sns/jscode2session";
		Map<String , String> map = new HashMap();
		map.put("appid",weChatProperties.getAppid());
		map.put("secret",weChatProperties.getSecret());
		map.put("js_code",userLoginDTO.getCode());
		map.put("grant_type","authorization_code");
		String json = HttpClientUtil.doGet(url,map);
		JSONObject jsonObject = JSON.parseObject(json);
		String openid = (String) jsonObject.get("openid");
		if(openid==null){
			throw new BaseException(MessageConstant.LOGIN_FAILED);
		}
			//根据openid查询用户对象
		User user = userMapper.findByOpenId(openid);
			//没有找到对应用户对象，将openid注册到用户表中（新用户注册）
		if(user == null){
			user = new User();
			user.setOpenid(openid);
			user.setCreateTime(LocalDateTime.now());
			userMapper.insert(user);
		}
		return user;

		}


	}



}
