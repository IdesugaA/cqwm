package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description DishFlavorMapper
 * @Author songyu
 * @Date 2023-09-24
 */
@Mapper
public interface DishFlavorMapper {
    /**
     * 批量写入菜品口味时间
     * @param flavors
     */
    void batchInserts(List<DishFlavor> flavors);

    /**
     * 批量删除菜品口味列表数据
     * @param dishIds
     */
    void deleteBatch(List<Long> dishIds);

    /**
     * 根据菜品id查询口味列表
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> findByDishId(Long dishId);
}
