package com.own.mapper;


import com.own.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper{


	@Select("select * from user where openid = #{openid}")
	User findByOpenId(String openid);

	@Insert("insert into user(openid,create_time) values(#{openid},#{createTime})")
	@Options(userGeneratedKeys = true , keyProperty = "id")
	void insert(User user)

	@Select("select * from user where id = #{id}")
	User findById(Long id);

	//根据时间范围查询用户数量
	Integer countByMap(Map map);

}
