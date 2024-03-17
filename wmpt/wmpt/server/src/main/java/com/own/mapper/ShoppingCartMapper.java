package com.own.mapper;


import com.own.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCart findByCondition(ShoppingCart shoppingCart);

    @Update("update shopping_cart set number=number+1 where id=#{id}")
    void updateNumber(ShoppingCart dbShoppingCart);

    @Insert("insert into shopping_cart values(null,#{name},#{image},#{userid}," +
            "#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> findListByUserId(Long userId);

    @Delete("delete from shopping_cart where user_id=#{userId}")
    void clean(Long userId);

    //批量插入购物车数据
    void insertBatch(List<ShoppingCart> shoppingCartList);

}
