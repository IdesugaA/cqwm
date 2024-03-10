package com.own.service;

import com.own.dto.EmployeeDTO;
import com.own.dto.EmployeeLoginDTO;
import com.own.dto.EmployeePageQueryDTO;
import com.own.entity.Employee;
import com.own.result.PageResult;

public interface EmployeeService {

    Employee login(EmployeeLoginDTO employeeLoginDTO);

    void add(EmployeeDTO employeeDTO);

    PageResult page(EmployeePageQueryDTO employeePageQueryDTO);

    void status(Integer status, Long id);

    void update(Employee employee);
}
