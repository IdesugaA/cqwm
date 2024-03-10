package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @Description ShoppingCartService
 * @Author songyu
 * @Date 2023-09-28
 */
public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 获取用户的购物车集合数据
     * @param userId
     * @return
     */
    List<ShoppingCart> findListByUserId(Long userId);

    /**
     * 清空购物车方法
     * @param userId
     */
    void clean(Long userId);
}
