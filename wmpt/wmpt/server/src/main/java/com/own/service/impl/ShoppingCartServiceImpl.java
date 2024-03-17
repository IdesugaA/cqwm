package com.own.service.impl;

import com.own.context.BaseContext;
import com.own.dto.ShoppingCartDTO;
import com.own.entity.Dish;
import com.own.entity.Setmeal;
import com.own.entity.ShoppingCart;
import com.own.mapper.DishMapper;
import com.own.mapper.SetmealMapper;
import com.own.mapper.ShoppingCartMapper;
import com.own.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    //添加购物车
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO){
        //查询当前商品是否添加过购物车
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        ShoppingCart dbShoppingCart = shoppingCartMapper.findByCondition(shoppingCart);

        if(dbShoppingCart!=null) {
            //添加过，则更新数量+1
            shoppingCartMapper.updateNumber(dbShoppingCart);
        }else{
            //没有添加过，添加新的购物车记录到数据库中
            String name = "";
            BigDecimal amount = null;
            String image = "";
            if(shoppingCartDTO.getDishId()!=null){
                //添加的如果是菜品，则要获取菜品信息的数据封装，插入到购物车记录中
                Dish dish = dishMapper.findById(shoppingCartDTO.getDishId());
                name = dish.getName();
                amount = dish.getPrice();
                image = dish.getImage();
            }else{
                //如果添加的是套餐，就要获取套餐信息的数据封装，插入到购物车记录中
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                name = setmeal.getName();
                amount = setmeal.getPrice();
                image = setmeal.getImage();
            }
            //插入购物车数据
            shoppingCart.setName(name);
            shoppingCart.setImage(image);
            shoppingCart.setNumber(1);
            shoppingCart.setAmount(amount);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    //获取购物车数据
    @Override
    public List<ShoppingCart> findListByUserId(Long userId){
        return shoppingCartMapper.findListByUserId(userId);
    }

    //清空购物车
    @Override
    public void clean(Long userId){
        shoppingCartMapper.clean(userId);
    }

}
