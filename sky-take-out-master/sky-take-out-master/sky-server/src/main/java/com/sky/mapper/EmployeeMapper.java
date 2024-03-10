package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee values(null,#{name},#{username},#{password}," +
            "#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime}," +
            "#{createUser},#{updateUser})")
    void insert(Employee employee);

    /**
     * 根据用户姓名查询员工列表数据
     * @param name
     * @return
     */
    List<Employee> page(String name);

    /**
     * 修改员工数据
     * @param employee
     */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据员工id查询员工对象
     * @param id
     * @return
     */
    @Select("select  * from employee where id = #{id}")
    Employee findById(Long id);
}
