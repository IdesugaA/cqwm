package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @Description ShopController
 * @Author songyu
 * @Date 2023-09-26
 */
@RestController
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "商家端店铺管理相关接口")
/**
 * 设置类构造器注入【spring官方推荐】
 *    1.类上 @RequiredArgsConstructor
 *    2.注入的成员要使用final标注
 */
@RequiredArgsConstructor
public class ShopController {

    private final RedisTemplate redisTemplate;

    /**
     * 处理商家端店铺设置请求
     * @return
     */
    @ApiOperation("设置店铺状态接口")
    @PutMapping("/{status}")
    public Result status(@PathVariable Integer status){
        log.info("开始设置店铺状态接口：{}",status);

        //写入缓存
        redisTemplate.opsForValue().set("SHOP_STATUS",status);

        //返回数据
        return Result.success();
    }

    /**
     * 处理商家端获取店铺状态请求
     * @return
     */
    @ApiOperation("商家端获取店铺状态接口")
    @GetMapping("/status")
    public Result getStatus(){
        log.info("开始执行商家端获取店铺状态...");

        //读取缓存数据
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        if(status == null){
            status = 0;
        }

        //返回数据
        return Result.success(status);
    }
}
