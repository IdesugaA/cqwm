package com.own.controller.user;


import com.own.constant.JwtClaimsConstant;
import com.own.dto.UserLoginDTO;
import com.own.entity.User;
import com.own.properties.JwtProperties;
import com.own.result.Result;
import com.own.service.UserService;
import com.own.utils.JwtUtil;
import com.own.vo.UserLoginVO;
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

@RestController
@RequestMapping("/user/user")
@Slf4j
@Api(tags="用户管理相关接口")
@RequiredArgsConstructor
//最后一个注解。会生成一个参数列表为final属性的构造方法
//非final就没有。配合@Controller就可以让容器调用
//这个构造方法，这样就省去@Autowired
public class UserController{

	private final UserService userService;

	private final JwtProperties jwtProperties;


	@ApiOperation("用户登录接口")
	@PostMapping("/login")
	public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
		log.info("开始执行用户登录接口：{}",userLoginDTO);
		User user = userService.login(userLoginDTO);
		//下发令牌
		Map<String , Object> claims = new HashMap();
		claims.put(JwtClaimsConstant.USER_ID,user.getId());
		claims.put("openId",user.getOpenid());
		String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(),claims);

		UserLoginVO userLoginVO = UserLoginVO.builder()
			.id(user.getId())
			.openid(user.getOpenid())
			.token(token)
			.build();
		return Result.success(userLoginVO);



	}


}
