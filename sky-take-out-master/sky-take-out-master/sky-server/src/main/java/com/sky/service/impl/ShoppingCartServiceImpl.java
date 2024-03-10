package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description ShoppingCartServiceImpl
 * @Author songyu
 * @Date 2023-09-28
 */
@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper shoppingCartMapper;

    private final DishMapper dishMapper;

    private final SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {

        //1.查询当前商品是否添加过购物车
        //sql语句分析：  select * from shopping_cart
        //              where user_id=xx and dish_id=xx and dish_flavor=xxx and setmeal_id=xx
        //将shoppingCartDTO封装到ShoppingCart对象中（只赋值3个属性）
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //封装登录人id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //根据登录人id,菜品id,菜品口味,套餐id查询已有的购物车商品记录
        ShoppingCart dbShoppingCart =  shoppingCartMapper.findByCondition(shoppingCart);

        if(dbShoppingCart!=null) {
            //2.添加过，更新数量+1
            //sql语句分析：update shopping_cart set number=number+1 where id=xx
            shoppingCartMapper.updateNumber(dbShoppingCart);
        }else {
            //3.没有添加过，添加新的购物车记录到数据库中
            //3.1 判断添加商品的类型（菜品 或 套餐）, 获取冗余字段数据
            String name = "";
            BigDecimal amount = null;
            String image = "";
            if(shoppingCartDTO.getDishId()!=null) {
                //3.2 如果添加的是菜品，就要获取菜品信息的数据封装，插入到购物车记录中
                //    封装菜品的商品名字、价格、图片
                //    sql：select * from dish where id = xx
                Dish dish = dishMapper.findById(shoppingCartDTO.getDishId());
                name = dish.getName();
                amount = dish.getPrice();
                image = dish.getImage();
            }else {
                //3.3 如果添加的是套餐，就要获取套餐信息的数据封装，插入到购物车记录中
                //    封装套餐的商品名字、价格、图片
                //    sql：select * from setmeal where id = xx
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                name = setmeal.getName();
                amount = setmeal.getPrice();
                image = setmeal.getImage();
            }

            //3.4 插入购物车记录
            //    sql：insert into shopping_cart values(...)
            shoppingCart.setName(name);
            shoppingCart.setImage(image);
            shoppingCart.setNumber(1);
            shoppingCart.setAmount(amount);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }

    }

    /**
     * 获取用户的购物车集合数据
     *
     * @param userId
     * @return
     */
    @Override
    public List<ShoppingCart> findListByUserId(Long userId) {
        return shoppingCartMapper.findListByUserId(userId);
    }

    /**
     * 清空购物车方法
     *
     * @param userId
     */
    @Override
    public void clean(Long userId) {
        shoppingCartMapper.clean(userId);
    }
}
