package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Api(tags = "员工相关接口")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @ApiOperation("员工登录的接口")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation("员工登出的接口")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    @GetMapping("/hello")
    public String hello(){
        return "8080";
    }

    /**
     * 处理员工新增请求
     * @param employeeDTO
     * @return
     */
    @ApiOperation("员工新增接口") //swagger需要
    @PostMapping
    public Result add(@RequestBody EmployeeDTO employeeDTO){

        log.info("开始执行员工新增接口：{}",employeeDTO);

        //调用业务实现员工新增功能
        employeeService.add(employeeDTO);

        //打印线程id
        log.info("JwtTokenAdminInterceptor线程id:{}",Thread.currentThread().getId());

        //返回数据
        return Result.success();
    }


    /**
     * 处理员工分页请求
     * @param employeePageQueryDTO
     * @return
     */
    @ApiOperation("员工分页接口")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){ //使用requestbody的前提是参数是json格式传过来的
        //但这里是get方法。如果是get，delete都没有请求体，put和post就要

        log.info("开始执行员工分页接口：{}",employeePageQueryDTO);

        //调用业务查询分页数据返回PageResult
        PageResult pageResult = employeeService.page(employeePageQueryDTO);

        //返回数据
        return Result.success(pageResult);
    }


    /**
     * 处理员工启用禁用请求
     * @param status 状态
     * @param id 员工id
     * @return
     */
    @ApiOperation("员工启用禁用接口")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status,Long id){

        log.info("开始执行员工启用禁用接口：status={},id={}",status,id);

        //调用service执行修改员工数据的方法
        employeeService.status(status,id);

        //返回数据
        return Result.success();
    }

    /**
     * 处理根据id查询员工请求
     * @param id
     * @return
     */
    @ApiOperation("根据id查询员工接口")
    @GetMapping("{id}")
    public Result<Employee> findById(@PathVariable Long id){
        log.info("开始执行根据id查询员工接口：{}",id);

        //调用业务根据id查询员工时间返回
        Employee employee = employeeService.findById(id);

        //返回数据
        return Result.success(employee);
    }

    /**
     * 处理修改员工请求
     * @param employee
     * @return
     */
    @ApiOperation("修改员工接口")
    @PutMapping
    public Result update(@RequestBody Employee employee){

        log.info("开始执行修改员工接口：{}",employee);
        //调用业务修改
        employeeService.update(employee);
        //返回数据
        return Result.success();
    }

}

