package com.own.controller.user;


import com.own.context.BaseContext;
import com.own.dto.ShoppingCartDTO;
import com.own.entity.ShoppingCart;
import com.own.result.Result;
import com.own.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.ReusableMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingcart")
@Slf4j
@Api(tags="购物车管理相关接口")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @ApiOperation("添加购物车接口")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("开始执行添加购物车接口：{}",shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    @ApiOperation("查询购物车接口")
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        log.info("开始执行查询购物车接口...");

        //调用业务后去用户购物车集合数据
        Long userId = BaseContext.getCurrentId();
        //根据用户id查询购物车集合数据
        List<ShoppingCart> shoppingCartList = shoppingCartService.findListByUserId(userId);
        return Result.success(shoppingCartList);
    }

    @ApiOperation("清除购物车接口")
    @DeleteMapping("/clean")
    public Result clean(){

        log.info("开始执行清除购物车接口...");

        Long userId = BaseContext.getCurrentId();

        shoppingCartService.clean(userId);

        return Result.success();

    }


}
