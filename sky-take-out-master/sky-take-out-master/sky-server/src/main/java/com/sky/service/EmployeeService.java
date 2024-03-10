package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工新增方法
     * @param employeeDTO
     */
    void add(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询数据
     * @param employeePageQueryDTO
     * @return
     */
    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     * @param status
     * @param id
     */
    void status(Integer status, Long id);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee findById(Long id);

    /**
     * 修改员工
     * @param employee
     */
    void update(Employee employee);
}
