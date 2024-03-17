package com.own.service;

import com.own.dto.DishDTO;
import com.own.dto.DishPageQueryDTO;
import com.own.entity.Dish;
import com.own.result.PageResult;
import com.own.vo.DishVO;

import java.util.List;

public interface DishService {

    void add(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO findById(Long id);

    void update(DishDTO dishDTO);

    void startOrStop(Integer status , Long id);

    List<DishVO> listWithFlavor(Dish dish);
}
