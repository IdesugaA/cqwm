package com.own.mapper;


import com.github.pagehelper.Page;
import com.own.annotation.AutoFill;
import com.own.dto.CategoryPageQueryDTO;
import com.own.entity.Category;
import com.own.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper{


	@AutoFill(OperationType.INSERT)
	@Insert("insert into category(type,name,sort,status,create_time,update_time,create_user,update_user)" +
			" VALUES" + " (#{type},#{name},#{sort},#{status}),#{createTime},#{updateTime},#{createUser},#{updateUser})")
	void insert(Category category);

	Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

	@Delete("delete from category where id = #{id}")
	void deleteById(Long id);

	List<Category> list(Integer type);

	@Select("select * from category where id = #{id}")
	Category findById(Long id);




}
