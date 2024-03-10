package com.own.service;

public interface SetmealService {

    //新增套餐，同时需要保存套餐和菜品的关联关系
    void saveWithDish(SetmealDTO setmealDTO);




}
