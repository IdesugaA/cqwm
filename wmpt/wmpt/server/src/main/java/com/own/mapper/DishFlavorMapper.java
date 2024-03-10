package com.own.mapper;

import com.own.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void batchInserts(List<DishFlavor> flavors);

    void deleteBatch(List<Long> dishIds);

    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> findByDishId(Long dishId);


}
