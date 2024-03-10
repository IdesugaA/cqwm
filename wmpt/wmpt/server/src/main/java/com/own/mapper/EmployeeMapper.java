package com.own.mapper;

import com.own.annotation.AutoFill;
import com.own.entity.Employee;
import com.own.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {


    @Select("select * from employee where username = #{username}")
    Employee getByUserName(String username);


    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee values(null,#{username},null,#{phone},#{sex},#{idNumber},#{status},"+
            "#{createTime},#{updateTime},#{createUser},#{updateUser},#{name})"
    )
    void insert(Employee employee);

    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    List<Employee> page(String name);
}
