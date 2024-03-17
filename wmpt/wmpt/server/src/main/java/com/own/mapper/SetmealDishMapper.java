package com.own.mapper;


import com.own.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper{
	//根据菜品ID列表查询关联套餐数量
	Integer countByDishIds(List<Long> dishIds);

	//根据菜品ID查询关联套餐ID列表
	List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

	//批量保存套餐和菜品的关联关系
	void insertBatch(List<SetmealDish> setmealDishes);

	//根据套餐ID删除套餐和菜品关联关系
	@Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
	void deleteBySetmealId(Long setmealId);

}
