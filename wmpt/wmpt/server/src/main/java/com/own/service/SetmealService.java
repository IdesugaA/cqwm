package com.own.service;

import com.own.dto.SetmealDTO;
import com.own.dto.SetmealPageQueryDTO;
import com.own.entity.Setmeal;
import com.own.result.PageResult;
import com.own.vo.DishItemVO;
import com.own.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    //新增套餐，同时需要保存套餐和菜品的关联关系
    void saveWithDish(SetmealDTO setmealDTO);


	//分页查询
	PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);


	//条件查询
	List<Setmeal> list(Setmeal setmeal);

	//根据id查询菜品选项
	List<DishItemVO> getDishItemById(Long id);

	void deleteBatch(List<Long> ids);

	//根据id查询套餐和关联的菜品数据
	SetmealVO getByIdWithDish(Long id);

	//修改套餐
	void update(SetmealDTO setmealDTO);

	void startOrStop(Integer status , Long id);

}
