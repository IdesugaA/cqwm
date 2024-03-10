package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description ShopController
 * @Author songyu
 * @Date 2023-09-26
 */
@RestController("userShopController") //注意要起别名，否则与admin下面的ShopController冲突
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "用户端店铺相关接口")
@RequiredArgsConstructor
public class ShopController {

    private final RedisTemplate redisTemplate;

    /**
     * 处理用户端获取店铺状态请求
     * @return
     */
    @ApiOperation("用户端获取店铺状态接口")
    @GetMapping("/status")
    public Result getStatus(){
        log.info("开始执行用户端获取店铺状态...");

        //读取缓存数据
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        if(status == null){
            status = 0;
        }

        //返回数据
        return Result.success(status);
    }
}
