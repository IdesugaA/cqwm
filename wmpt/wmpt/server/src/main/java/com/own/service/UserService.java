package com.own.service;


import com.own.dto.UserLoginDTO;
import com.own.entity.User;

public interface UserService{


	User login(UserLoginDTO userLoginDTO);


}
