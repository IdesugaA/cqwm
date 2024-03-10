package com.own.service.impl;

import com.own.dto.SetmealDTO;
import com.own.entity.Setmeal;
import com.own.entity.SetmealDish;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

public class SetmealServiceImpl {


    @CacheEvict(cacheNames="setmealCache" , key="#setmealDTO.categoryId")
    public void saveWithDish(SetmealDTO setmealDTO){
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);

        //向套餐表插入数据
        setmealMapper.insert(setmeal);

        //获取生成的套餐id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });


    }


}
