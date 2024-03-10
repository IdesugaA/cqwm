package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

/**
 * @Description ShoppingCartMapper
 * @Author songyu
 * @Date 2023-09-28
 */
@Mapper
public interface ShoppingCartMapper {
    /**
     * 根据条件查询购物车商品记录
     * @param shoppingCart
     * @return
     */
    ShoppingCart findByCondition(ShoppingCart shoppingCart);

    /**
     * 更新数量
     * @param dbShoppingCart
     */
    @Update("update shopping_cart set number=number+1 where id=#{id}")
    void updateNumber(ShoppingCart dbShoppingCart);

    @Insert("insert into shopping_cart values(null,#{name},#{image},#{userId}," +
            "#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 查询用户购物车集合
     * @param userId
     * @return
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> findListByUserId(Long userId);

    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void clean(Long userId);

    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
