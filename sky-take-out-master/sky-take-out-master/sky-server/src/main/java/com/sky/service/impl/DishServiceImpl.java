package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.BaseException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description DishServiceImpl
 * @Author songyu
 * @Date 2023-09-24
 */
@Service
public class DishServiceImpl implements DishService {

    @Resource
    private DishMapper dishMapper;

    @Resource
    private DishFlavorMapper dishFlavorMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SetmealDishMapper setmealDishMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * 缓存数据一致性问题：
     *      介绍：如何保证缓存数据与mysql数据的一致性
     *      解决方案：
     *          方案1：对mysql数据库做了任何的增删改，同时对redis缓存时间也做增删改【不推荐】
     *          方案2：数据库有任何改变，对redis缓存数据删除，下一次查询会自动重新缓存就更新了数据。【推荐】
     *                删除是原则：能删少就不要删多。
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void add(DishDTO dishDTO) {

        //1.插入菜品数据
        //将前端传入的dishDTO的数据赋值给dish对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //调用mapper插入,确保插入自动获取自增长的主键值
        dishMapper.insert(dish);

        //2.插入菜品口味列表数据
        //如果口味菜插入，没有口味抛出异常结束程序
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(CollectionUtils.isEmpty(flavors)){
           throw new BaseException("口味数据不能为空");
        }
        //flavors前端传入过来，里面dishId数据为null,补全dishId
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dish.getId());
        });
        //批量插入到数据库
        dishFlavorMapper.batchInserts(flavors);

        //清理缓存
        //分析：新增一个菜品只会影响一个分类，也就是影响一个缓存key,所以删除一个缓存数据就可以
        String key = "dish_"+dishDTO.getCategoryId();
        cleanChace(key);
    }

    /**
     * 分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //sql分析：查询菜品和分类表数据
        //先分页查询菜品数据：select * from dish where 条件
        //遍历菜品列表获取每个菜品的分类id,根据分类id查询分类数据得到分类名称：
        //          select * from category where id = #{categoryId}

        //1.对菜品分页设置分页参数
        PageHelper.startPage(dishPageQueryDTO.getPage(),
                dishPageQueryDTO.getPageSize());

        //2.执行菜品数据查询(dishList本质就是当前页数据列表)
        List<Dish> dishList = dishMapper.findByCondition(dishPageQueryDTO);
        Page<Dish> page = (Page<Dish>) dishList;

        //3.遍历菜品列表封装每个菜品的分类名字
        List<DishVO> dishVOList = dishList.stream().map(dish -> {
            DishVO dishVO = new DishVO();
            //将dish的数据赋值给dishVO
            BeanUtils.copyProperties(dish,dishVO);
            //根据菜品分类id查询分类数据Category
            Category category = categoryMapper.findById(dish.getCategoryId());
            if(category!=null) {
                //封装给dishVO设置分类名字
                dishVO.setCategoryName(category.getName());
            }
            return dishVO;
        }).collect(Collectors.toList());

        //4.封装PageResult返回数据
        return PageResult.builder()
                .total(page.getTotal())
                .records(dishVOList)
                .build();
        //企业开发实践：如果表中数量大：推荐多条sql，否则推荐一条表连接sql
    }

    /**
     * 菜品删除的方法
     *
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> ids) {
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
        //数量大于0，抛出异常，提示“有关联的套餐，菜品无法删除”
        if(count>0){
            throw new BaseException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //3.以上都没有，先删除菜品
        //sql分析：delete from dish where id in (id1,id2,...)
        dishMapper.deleteBatch(ids);

        //4.删除菜品口味列表数据
        //sql分析：delete from dish_flavor where dish_id in (dishId1,dishId2,...)
        dishFlavorMapper.deleteBatch(ids);
        //清理缓存
        //分析：批量删除菜品只会影响多个分类，所以要将“dish_*”开头全部删除
        cleanChace("dish_*");
    }

    private void cleanChace(String key) {
        //清理缓存
        Set set = redisTemplate.keys(key);
        redisTemplate.delete(set);
    }

    /**
     * 根据id查询菜品数据
     *
     * @param id
     * @return
     */
    @Override
    public DishVO findById(Long id) {
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

    /**
     * 修改菜品
     *
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(DishDTO dishDTO) {

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
        dishDTO.getFlavors().forEach(dishFlavor -> {
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

    /**
     * 菜品起售停售
     *
     * @param status
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void startOrStop(Integer status, Long id) {
        //sql: update dish set status = #{status}  wherw id = #{id}
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        if (status == StatusConstant.DISABLE) {
            // 如果是停售操作，还需要将包含当前菜品的套餐也停售
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if (setmealIds != null && setmealIds.size() > 0) {
                for (Long setmealId : setmealIds) {
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }

        //清理缓存
        //分析：虽然启售禁售只影响一个分类，但是这里没有分类id,所以全删
        cleanChace("dish_*");
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.findByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
