package com.own.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.own.constant.MessageConstant;
import com.own.constant.PasswordConstant;
import com.own.constant.StatusConstant;
import com.own.context.BaseContext;
import com.own.dto.EmployeeDTO;
import com.own.dto.EmployeeLoginDTO;
import com.own.dto.EmployeePageQueryDTO;
import com.own.entity.Employee;
import com.own.exception.AccountLockedException;
import com.own.exception.AccountNotFoundException;
import com.own.exception.PasswordErrorException;
import com.own.mapper.EmployeeMapper;
import com.own.result.PageResult;
import com.own.result.Result;
import com.own.service.EmployeeService;
import io.swagger.annotations.ApiOperation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.time.LocalDateTime;
import java.util.List;
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Value("${own.md5.salt}")
    private String salt;

    public Employee login(EmployeeLoginDTO employeeLoginDTO){
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        Employee employee = employeeMapper.getByUserName(username);
        if(employee == null){
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        System.out.println("password:"+password);
        if(!password.equals(employee.getPassword())){
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if(employee.getStatus() == StatusConstant.DISABLE){
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        return employee;


    }

    @Override
    public void add(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        employee.setPassword(DigestUtils.md5DigestAsHex((PasswordConstant.DEFAULT_PASSWORD+salt).getBytes()));
        //注意，EmployeeDTO和Employee都没有password属性，这个是由自己设置的
        employee.setStatus(StatusConstant.ENABLE);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = BaseContext.getCurrentId();
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeMapper.insert(employee);
    }

    @Override
    public void status(Integer status, Long id) {
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();
        employeeMapper.update(employee);
    }

    @Override
    public void update(Employee employee) {
        employeeMapper.update(employee);
    }

    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO){
        PageHelper.startPage(employeePageQueryDTO.getPage(),
                employeePageQueryDTO.getPageSize());
        List<Employee> employeeList = employeeMapper.page(employeePageQueryDTO.getName());
        Page<Employee> page = (Page<Employee>) employeeList;
        return PageResult.builder().total(page.getTotal()).records(page.getResult()).build();
    }


}
