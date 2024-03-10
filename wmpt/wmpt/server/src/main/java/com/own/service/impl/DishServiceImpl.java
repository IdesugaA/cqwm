package com.own.service.impl;

import com.alibaba.excel.util.BeanMapUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.own.constant.MessageConstant;
import com.own.constant.StatusConstant;
import com.own.dto.DishDTO;
import com.own.dto.DishPageQueryDTO;
import com.own.entity.Category;
import com.own.entity.Dish;
import com.own.entity.DishFlavor;
import com.own.exception.BaseException;
import com.own.mapper.DishFlavorMapper;
import com.own.mapper.DishMapper;
import com.own.result.PageResult;
import com.own.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void add(DishDTO dishDTO){
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        dishMapper.insert(dish);

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(CollectionUtils.isEmpty(flavors)){
            throws new BaseException("口味数据不能为空");
        }

        flavors.forEach(dishFlavor -> {
           dishFlavor.setDishId(dish.getId());
        });

        dishFlavorMapper.batchInserts(flavors);

        String key = "dish_" + dishDTO.getCategoryId();
        cleanChace(key);

    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO){

        //设置菜品分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(),
                dishPageQueryDTO.getPageSize());


        //执行菜品数据查询
        List<Dish> dishList = dishMapper.findByCondition(dishPageQueryDTO);
        Page<Dish> page = (Page<Dish>) dishList;

        //遍历菜品列表封装每个菜品的分类名字

        List<DishVO> dishVOList = dishList.stream().map(dish ->{
           DishVO dishVO = new DishVO();
           BeanUtils.copyProperties(dish,dishVO);
           Category category = categoryMapper.findById(dish.getCategoryId());
           if(category!=null){
               dishVO.setCategoryName(category.getName());
           }
           return dishVO;
        }).collect(Collectors.toList());

        return PageResult.builder()
                .total(page.getTotal())
                .records(dishVOList)
                .build();
                //企业开发实践：如果表中数量大：推荐多条sql，否则推荐一条表连接sql



    }

    public void delete(List<Long> ids){
        //1.判断删除的菜品是否有启售的时间
        //根据菜品id查询启售菜品的数量
        Integer count = dishMapper.countByIds(ids);
        //数量大于0，抛出异常，提示“有启售的菜品无法删除”
        if(count>0){
            throw new BaseException(MessageConstant.DISH_ON_SALE);
        }

        //2.判断删除的菜品是否有关联套餐数据
        //根据菜品id查询关联的套餐数量
        count = setmealDishMapper.countByDishIds(ids);
        if(count>0){
            throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3.以上都没有，先删除菜品
        //sql分析：delete from dish where id in (id1,id2,...)
        dishMapper.deleteBatch(ids);

        dishFlavorMapper.deleteBatch(ids);

        //清理缓存
        //分析：批量删除菜品只会影响多个分类，所以要将“dish_*”开头全部删除
        cleanChace("dish_*");

    }

    @Override
    public DishVO findById(Long id){

        //1.根据菜品id查询菜品对象
        Dish dish = dishMapper.findById(id);

        //2.根据菜品id查询口味列表集合数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.findByDishId(id);

        //3.将上面数据封装到DishVO中 返回
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;

    }

    @Override
    public void update(DishDTO dishDTO){

        //1.修改菜品数据
        //将dishDTO赋值给dish对象数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //调用mapper修改菜品
        dishMapper.update(dish);

        //2.修改口味列表数据
        //先根据菜品id删除对应口味列表数据
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteBatch(ids);

        //遍历前端的口味列表数据，设置关联菜品id
        dishDTO.getFlavors().forEach(dishFlavor ->{
           dishFlavor.setDishId(dish.getId());
        });

        //批量将前端的口味列表数据插入数据库
        dishFlavorMapper.batchInserts(dishDTO.getFlavors());

        //清理缓存
        //分析：修改影响到多个分类，所以删除dish_*的数据
        cleanChace("dish_*");


    }

    @Resource
    private SetmealMapper setmealMapper;

    public void startOrStop(Integer status , Long id){
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();  //好处是赋值的时候更清晰
        dishMapper.update(dish);

        if(status == StatusConstant.DISABLE){
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);

            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if(setmealIds != null && setmealIds.size() > 0){
                for(Long setmealId :setmealIds){
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }

        cleanChace("dish_*");
    }

    //根据分类ID查询菜品
    public List<Dish> list(Long categoryId){
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    //条件查询菜品和口味
    public List<DishVO> listWithFlavor(Dish dish){
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for(Dish d : dishList){
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.findByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


    private void cleanChace(String key){
        Set set = redisTemplate.keys(key);
        redisTemplate.delete(key);
    }

}
