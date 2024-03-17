package com.own.controller.user;


import com.own.constant.StatusConstant;
import com.own.entity.Dish;
import com.own.result.Result;
import com.own.service.DishService;
import com.own.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags="C端-菜品浏览接口")
public class DishController{

	@Autowired
	private DishService dishService;

	@Autowired
	private RedisTemplate redisTemplate;

	@GetMapping("/list")
	@ApiOperation("根据分类id查询菜品")
	public Result<List<DishVO>> list(Long categoryId){
		//目标：实现查询菜品缓存逻辑
		//1.查询缓存是否有数据
		//定义key
		String key = "dish_" + categoryId;
		//查询缓存
		List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
		if(CollectionUtils.isEmpty(list)){
			//如果缓存没有数据，执行数据库查询
			Dish dish = new Dish();
			dish.setCategoryId(categoryId);
			dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
			list = dishService.listWithFlavor(dish);
			//从数据库获取的数据写入缓存
			redisTemplate.opsForValue().set(key,list);
		}
		return Result.success(list);

	}

}
