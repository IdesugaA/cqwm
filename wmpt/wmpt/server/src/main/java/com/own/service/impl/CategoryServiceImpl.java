package com.own.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.page.PageMethod;
import com.own.constant.MessageConstant;
import com.own.constant.StatusConstant;
import com.own.dto.CategoryDTO;
import com.own.dto.CategoryPageQueryDTO;
import com.own.entity.Category;
import com.own.exception.DeletionNotAllowedException;
import com.own.mapper.CategoryMapper;
import com.own.mapper.DishMapper;
import com.own.mapper.SetmealMapper;
import com.own.result.PageResult;
import com.own.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryMapper categoryMapper;

	@Autowired
	private DishMapper dishMapper;

	@Autowired
	private SetmealMapper setmealMapper;

	//新增分类
	public void save(CategoryDTO categoryDTO){
		Category category = new Category();

		//属性拷贝
		BeanUtils.copyProperties(categoryDTO,category);
		
		//分类状态默认为禁用状态0
		category.setStatus(StatusConstant.DISABLE);
		categoryMapper.insert(category);

	}

	//分页查询
	
	public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
		PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
		//下一条sql进行分页，自动加入limit关键字分页
		Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

		return new PageResult(page.getTotal(),page.getResult());
	}

	//根据id删除分类
	public void deleteById(Long id){
		//查询当前分类是否关联了菜品，如果是，那就抛出业务异常
		Integer count = dishMapper.countByCategoryId(id);
		if(count > 0){
			//当前分类下有菜品，不能删除
			throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH;
		}

		categoryMapper.deleteById(id);
	}

	//修改分类
	public void update(CategoryDTO categoryDTO){
		Category category = new Category();
		BeanUtils.copyProperties(categoryDTO,category);
		categoryMapper.update(category);
	}


	//启用，禁用分类
	public void startOrStop(Integer status,Long id){
		Category category = Category.builder()
			.id(id)
			.status(status)
			.build();
		categoryMapper.update(category);
	}

	//根据类型查询分类
	public List<Category> list(Integer type){
		return categoryMapper.list(type);
	}


}
