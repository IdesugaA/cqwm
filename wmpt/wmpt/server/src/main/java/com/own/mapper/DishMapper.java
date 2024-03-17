package com.own.mapper;


import com.own.annotation.AutoFill;
import com.own.dto.DishPageQueryDTO;
import com.own.entity.Dish;
import com.own.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    //根据分类id查询菜品数量
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     *
     *
     *
     */

    @AutoFill(OperationType.INSERT)
    @Insert("insert into dish values(null,#{name},#{categoryId},#{price}," +
            "#{image},#{description},#{status},#{createTime},#{updateTime}," +
            "#{createUser},#{updateUser})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(Dish dish);

    //条件查询菜品列表数据
    List<Dish> findByCondition(DishPageQueryDTO dishPageQueryDTO);

    //根据菜品id查询启售的数量
    Integer countByIds(List<Long> ids);

    //根据id查询菜品
    @Select("select * from dish where id = #{id}")
    Dish findById(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    //动态条件查询菜品
    List<Dish> list(Dish dish);

    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetMealId(Long setmealId);


    //根据条件统计菜品数量
    Integer countByMap(Map map);

    void deleteBatch(List<Long> ids);





}
