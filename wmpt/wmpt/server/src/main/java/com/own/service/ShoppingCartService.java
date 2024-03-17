package com.own.service;

import com.own.dto.ShoppingCartDTO;
import com.own.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    void add(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> findListByUserId(Long userId);

    void clean(Long userId);


}
