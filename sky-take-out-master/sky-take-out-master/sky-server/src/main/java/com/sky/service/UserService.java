package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @Description UserService
 * @Author songyu
 * @Date 2023-09-27
 */
public interface UserService {

    /**
     * 用户登录的方法
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);
}
