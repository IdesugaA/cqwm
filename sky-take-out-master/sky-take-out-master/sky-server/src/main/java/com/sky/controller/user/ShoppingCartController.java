package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description ShoppingCartController
 * @Author songyu
 * @Date 2023-09-28
 */
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api(tags = "购物车管理相关接口")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    /**
     * 处理添加购物车请求
     * @param shoppingCartDTO
     * @return
     */
    @ApiOperation("添加购物车接口")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){

        log.info("开始执行添加购物车接口：{}",shoppingCartDTO);

        //调用业务添加购物车
        shoppingCartService.add(shoppingCartDTO);

        //返回数据
        return Result.success();
    }

    /**
     * 处理查询购物车请求
     * @return
     */
    @ApiOperation("查询购物车接口")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){

        log.info("开始执行查询购物车接口...");

        //调用业务获取用户购物车集合数据
        //获取登录用户id
        Long userId = BaseContext.getCurrentId();

        //根据用户id查询购物车集合数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.findListByUserId(userId);

        //返回数据
        return Result.success(shoppingCartList);
    }

    /**
     * 处理清除购物车请求
     * @return
     */
    @ApiOperation("清除购物车接口")
    @DeleteMapping("/clean")
    public Result clean(){

        log.info("开始执行清除购物车接口...");

        //调用业务清除用户购物车集合数据
        //获取登录用户id
        Long userId = BaseContext.getCurrentId();

        //根据用户id清除购物车集合数据
        shoppingCartService.clean(userId);

        //返回数据
        return Result.success();
    }

}
