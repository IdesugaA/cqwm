package com.own.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags="套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        setmealService.saveWithDish(setmealDTO);
        log.info(setmealService.getClass().getName());;
        return Result.success();
    }
	
	@ApiOperation("分页查询")
	@GetMapping("/page")
	public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
		PageResult pageResult = setmealService.pageQUery(setmealPageQueryDTO);
		return Result.success(pageResult);
	}

	@ApiOperation("根据ID查询套餐")
	@GetMapping("/{id}")
	public Result<SetmealVO> getById(@PathVariable Long id){
		SetmealVO setmealVO = setmealService.getByIdWithDish(id);
		return Result.success(setmealVO);
	}
	//用DTO传过来，用VO传出去
	
	@ApiOperation("修改套餐")
	@PutMapping
	public Result update(@RequestBody SetmealDTO setmealDTO){
		setmealService.update(setmealDTO);
		return Result.success();
	}

	@ApiOperation
	@PostMapping("/status/{status}")
	public Result startOrStop(@PathVariable Integer status , Long id){
		setmealService.startOrStop(status,id);
		return Result.success();
	}

}
