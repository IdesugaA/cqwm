package com.own.controller.admin;


import com.own.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
@Slf4j
@Api(targs="商家端店铺管理相关接口")
@RequiredArgsConstructor
public class ShopController{

	private final RedisTemplate redisTemplate;

	//处理商家端店铺设置请求
	@PutMapping("/{status}")
	public Result status(@PathVariable Integer status){
		log.info("开始设置店铺状态接口：{}",status);
		//写入缓存
		redisTemplate.opsForValue().set("SHOP_STATUS",status);
		//返回数据
		return Result.success();
	}

	//处理商家端获取店铺状态请求
	@ApiOperation("商家端获取店铺状态接口")
	@GetMapping("/status")
	public Result getStatus(){
		log.info("开始执行商家端获取店铺状态");
		//读取缓存数据
		Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
		if(status == null){
			status = 0;
		}

		return Result.success(status);
	}

}
