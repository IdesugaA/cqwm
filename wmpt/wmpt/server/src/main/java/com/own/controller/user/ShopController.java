package com.own.controller.user;


import com.own.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController") // 起别名的原因是防止与admin下面的ShopController起冲突
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "用户端店铺相关接口")
@RequiredArgsConstructor
public class ShopController {

    private final RedisTemplate redisTemplate;

    //处理用户端获取店铺状态请求
    @ApiOperation("用户端获取店铺状态接口")
    @GetMapping("/status")
    public Result getStatus(){
        log.info("开始执行用户端获取店铺状态");
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");
        if(status == null){
            status = 0;
        }
        return Result.success(status);
    }


}
