package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Description SetmealDishMapper
 * @Author songyu
 * @Date 2023-09-24
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id列表查询关联套餐数量
     * @param dishIds
     * @return
     */
    Integer countByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id查询关联套餐id列表
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
