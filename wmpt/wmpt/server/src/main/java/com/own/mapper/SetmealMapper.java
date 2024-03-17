package com.own.mapper;

import com.github.pagehelper.Page;
import com.own.annotation.AutoFill;
import com.own.dto.SetmealPageQueryDTO;
import com.own.entity.Setmeal;
import com.own.enumeration.OperationType;
import com.own.vo.DishItemVO;
import com.own.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper

public interface SetmealMapper{
	//根据分类id查询套餐的数量
	@Select("select count(id) from setmeal where category_id = #{categoryId}")
	Integer countByCategoryId(Long id);

	//修改套餐数据
	void update(Setmeal setmeal);

	//新增套餐
	@AutoFill(OperationType.INSERT)
	void insert(Setmeal setmeal);

	//分页查询
	Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

	//动态条件查询套餐
	List<Setmeal> list(Setmeal setmeal);

	//根据套餐ID查询菜品选项
	@Select("select sd.name,sd.copies,d.image,d.description"+"from setmeal_dish sd left join dish d on sd.dish_id = d.id" + "where sd.setmeal_id = #{setmealId}")
	List<DishItemVO> getDishItemBySetmealId(Long setmealId);

	//根据id查询套餐
	@Select("select * from setmeal where id = #{id}")
	Setmeal getById(Long id);

	//根据id删除套餐
	@Delete("delete from setmeal where id = #{id}")
	void deleteById(Long setmealId);

	//根据id查询套餐和套餐菜品关系
	SetmealVO getByIdWithDish(Long id);

	//根据条件统计套餐数量
	Integer countByMap(Map map);
}
