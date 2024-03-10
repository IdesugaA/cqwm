package com.own.controller.admin;


import com.own.dto.DishDTO;
import com.own.dto.DishPageQueryDTO;
import com.own.entity.Dish;
import com.own.result.PageResult;
import com.own.result.Result;
import com.own.service.DishService;
import com.own.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags="菜品相关接口")
public class DishController {

    @Resource
    private DishService dishService;

    @ApiOperation("菜品新增接口")
    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO){

        log.info("开始执行菜品新增接口：{}",dishDTO);

        dishService.add(dishDTO);

        return Result.success();

    }

    @ApiOperation("菜品分页查询接口")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){

        log.info("开始执行菜品分页查询接口：{}",dishPageQueryDTO);

        PageResult pageResult = dishService.page(dishPageQueryDTO);

        return Result.success(pageResult);

    }

    @ApiOperation("菜品删除接口")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){
        log.info("开始执行菜品删除接口：{}",ids);
        dishService.delete(ids);
        return Result.success();
    }

    @ApiOperation("根据id查询菜品接口")
    @GetMapping("/{id}")
    public Result<DishVO> findById(@PathVariable Long id){
        log.info("开始执行根据id查询菜品接口：{}",id);

        DishVO dishVO = dishService.findById(id);

        return Result.success(dishVO);
    }

    @ApiOperation("修改菜品接口")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("开始执行修改菜品接口：{}",dishDTO);

        dishService.update(dishDTO);

        return Result.success();
    }

    @ApiOperation("菜品起售停售")
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status , Long id){
        dishService.startOrStop(status,id);
        return Result.success();
    }

    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }




}
