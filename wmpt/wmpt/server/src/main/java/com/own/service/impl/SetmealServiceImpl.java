package com.own.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.own.constant.MessageConstant;
import com.own.constant.StatusConstant;
import com.own.dto.SetmealDTO;
import com.own.dto.SetmealPageQueryDTO;
import com.own.entity.Dish;
import com.own.entity.Setmeal;
import com.own.entity.SetmealDish;
import com.own.exception.DeletionNotAllowedException;
import com.own.exception.SetmealEnableFailedException;
import com.own.mapper.DishMapper;
import com.own.mapper.SetmealDishMapper;
import com.own.mapper.SetmealMapper;
import com.own.result.PageResult;
import com.own.service.SetmealService;
import com.own.vo.DishItemVO;
import com.own.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public class SetmealServiceImpl implements SetmealService {

	@Autowired
	private SetmealMapper setmealMapper;

	@Autowired
	private SetmealDishMapper setmealDishMapper;

	@Autowired
	private DishMapper dishMapper;

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
		
		//保存套餐和菜品的关联关系
		setmealDishMapper.insertBatch(setmealDishes);
		//insert方法可以批量保存套餐和菜品的关联关系,setmealDishes里每个对象都有它对应的套餐的ID，所以才可以批量保存

	}

		//分页查询
		public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
			//用一个DTO实体来保存查询信息


			//传到service层也是DTO
			int pageNum = setmealPageQueryDTO.getPage();
			int pageSize = setmealPageQueryDTO.getPageSize();
			PageHelper.startPage(pageNum,pageSize);
			Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
			return new PageResult(page.getTotal(),page.getResult());
		}
    	
		//条件查询
		@Cacheable(cacheNames = "setmealCache",key="#setmeal.categoryId",unless="#result==null")
		public List<Setmeal> list(Setmeal setmeal){
			List<Setmeal> list = setmealMapper.list(setmeal);
			return list;
		}

		//根据id查询菜品选项
		public List<DishItemVO> getDishItemById(Long id){
			return setmealMapper.getDishItemBySetmealId(id);
		}

		//批量删除套餐
		@CacheEvict(cacheNames="setmealCache",allEntries=true)
		public void deleteBatch(List<Long> ids){
			//先判断能不能删除，判断标准是套餐中的某个菜品是否处在启售状态中
			ids.forEach(id ->{
				Setmeal setmeal = setmealMapper.getById(id);
				if(StatusConstant.ENABLE == setmeal.getStatus()){
				//起售中的套餐不能删除
				throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
				}		
			});
			//删除套餐要注意两个方面
			ids.forEach(setmealId ->{
				//删除套餐表中的数据
				setmealMapper.deleteById(setmealId);
				//删除套餐菜品关系表中的数据
				setmealDishMapper.deleteBySetmealId(setmealId);		
			});
		}


		//根据ID查询套餐和套餐菜品关系
		public SetmealVO getByIdWithDish(Long id){
			SetmealVO setmealVO = setmealMapper.getByIdWithDish(id);
			return setmealVO;
		}

		//修改套餐
		@CacheEvict(cacheNames="setmealCache",allEntries=true)
		public void update(SetmealDTO setmealDTO){
			Setmeal setmeal = new Setmeal();
			BeanUtils.copyProperties(setmealDTO,setmeal);
			//修改套餐表，执行update
			setmealMapper.update(setmeal);

			//套餐id
			Long setmealId = setmealDTO.getId();

			//删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
			setmealDishMapper.deleteBySetmealId(setmealId);
			List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
			setmealDishes.forEach(setmealDish ->{
				setmealDish.setSetmealId(setmealId);
			});

			//重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
			setmealDishMapper.insertBatch(setmealDishes);
		}

		//套餐起售，停售
		public void startOrStop(Integer status , Long id){
			//起售套餐时，判断套餐内是否有停售菜，有停售菜提示"套餐内包含未起售菜品，无法起售"
			if(status == StatusConstant.ENABLE){
				//select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = ?
				List<Dish> dishList = dishMapper.getBySetMealId(id);
				if(dishList != null && dishList.size() > 0){
					dishList.forEach(dish ->{
						if(StatusConstant.DISABLE == dish.getStatus()){
							throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
						}		
					});
				}
			}
			//如果是要停售，则无需判断其他
			Setmeal setmeal = Setmeal.builder()
					.id(id)
					.status(status)
					.build();
			setmealMapper.update(setmeal);
		}



}
