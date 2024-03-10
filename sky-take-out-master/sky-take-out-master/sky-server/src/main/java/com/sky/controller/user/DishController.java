package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {

        //目标：实现查询菜品缓存逻辑

        //1.查询缓存是否有数据
        //定义key
        String key = "dish_"+categoryId;
        //查询缓存
        List<DishVO> list  = (List<DishVO>) redisTemplate.opsForValue().get(key);

        if(CollectionUtils.isEmpty(list)) {
            //1.1 缓存如果没有数据，执行数据库查询
            Dish dish = new Dish();
            dish.setCategoryId(categoryId);
            dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

            list = dishService.listWithFlavor(dish);

            //1.2 从数据库获取的数据写入缓存
            redisTemplate.opsForValue().set(key,list);
        }

        //2.返回数据
        return Result.success(list);
    }

}
