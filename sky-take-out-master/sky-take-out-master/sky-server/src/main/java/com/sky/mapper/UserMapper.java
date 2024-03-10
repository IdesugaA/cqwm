package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @Description UserMapper
 * @Author songyu
 * @Date 2023-09-27
 */
@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User findByOpenId(String openid);

    /**
     * 插入用户数据
     * @param user
     */
    @Insert("insert into user(openid,create_time) values(#{openid},#{createTime})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(User user);

    /**
     * 根据id获取用户对象数据
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User findById(Long id);

    /**
     * 根据时间范围查询用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
