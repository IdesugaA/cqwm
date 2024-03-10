package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description 处理菜品相关请求
 * @Author songyu
 * @Date 2023-09-24
 */
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {

    @Resource
    private DishService dishService;

    /**
     * 处理菜品新增请求
     * @param dishDTO
     * @return
     */
    @ApiOperation("菜品新增接口")
    @PostMapping
    public Result add(@RequestBody DishDTO dishDTO){

        log.info("开始执行菜品新增接口：{}",dishDTO);

        //执行业务新增菜品
        dishService.add(dishDTO);

        //返回数据
        return Result.success();
    }

    /**
     * 处理菜品分页查询请求
     * @param dishPageQueryDTO
     * @return
     */
    @ApiOperation("菜品分页查询接口")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){

        log.info("开始执行菜品分页查询接口：{}",dishPageQueryDTO);

        //调用业务获取分页查询数据对象
        PageResult  pageResult = dishService.page(dishPageQueryDTO);
        //返回
        return Result.success(pageResult);
    }

    /**
     * 处理菜品删除请求（单个删除，批量删除）
     * @param ids
     * @return
     */
    @ApiOperation("菜品删除接口")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){

        log.info("开始执行菜品删除接口：{}",ids);

        //调用service执行删除操作
        dishService.delete(ids);

        //返回数据
        return Result.success();
    }

    /**
     * 处理根据id查询菜品请求
     * @param id
     * @return
     */
    @ApiOperation("根据id查询菜品接口")
    @GetMapping("/{id}")
    public Result<DishVO> findById(@PathVariable Long id){

        log.info("开始执行根据id查询菜品接口：{}",id);

        //调用业务查询菜品数据
        DishVO dishVO = dishService.findById(id);

        //返回数据
        return Result.success(dishVO);
    }

    /**
     * 处理修改菜品请求
     * @param dishDTO
     * @return
     */
    @ApiOperation("修改菜品接口")
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){

        log.info("开始执行修改菜品接口：{}",dishDTO);

        //调用业务执行菜品修改
        dishService.update(dishDTO);

        //返回数据
        return Result.success();
    }

    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        dishService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

}
