package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description UserController
 * @Author songyu
 * @Date 2023-09-27
 */
@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags = "用户管理相关接口")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final JwtProperties jwtProperties;

    /**
     * 处理用户登录请求
     * @param userLoginDTO
     * @return
     */
    @ApiOperation("用户登录接口")
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("开始执行用户登录接口：{}",userLoginDTO);

        //调用业务指定登录方法，返回登录的用户数据
        User user = userService.login(userLoginDTO);

        //下发令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        claims.put("openId",user.getOpenid());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        //封装UserLoginVO数据
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        //返回数据
        return Result.success(userLoginVO);
    }

}
