package com.own.controller.admin;


import com.own.constant.JwtClaimsConstant;
import com.own.dto.EmployeeDTO;
import com.own.dto.EmployeeLoginDTO;
import com.own.dto.EmployeePageQueryDTO;
import com.own.entity.Employee;
import com.own.properties.JwtProperties;
import com.own.result.PageResult;
import com.own.result.Result;
import com.own.service.EmployeeService;
import com.own.utils.JwtUtil;
import com.own.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/employee")
@Api(tags="员工相关接口")
@Slf4j
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    JwtProperties jwtProperties;


    //DTP的参数对应HTTP各项参数
    @PostMapping("/login")
    @ApiOperation("员工登录接口")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        //后端@RequestBody注解对应的类在将HTTP的输入流(含请求体)装配到目标类(即:@RequestBody后面的类)时，会根据
        // json字符串中的key来匹配对应实体类的属性，如果匹配一致且json中的该key对应的值
        //符合(或可转换为)实体类的对应属性的类型要求时，会调用实体类的setter方法将值赋给该属性。
        log.info("员工登录：{}",employeeLoginDTO);
        Employee employee = employeeService.login(employeeLoginDTO);

        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID,employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );
        //jwt有三个部分，一是头部，二是有效载荷，三是签名哈希。头部和有效载荷会通过算法进行加密，然后再对两部分进行哈希计算
        //得到哈希值，以确定两部分没有被修改

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);  //本来需要一个json解析库的，但spring-boot-starter-web内部就继承了jackson库
        //所以才能顺利返回一个对象然后解析成json

        //VO，DTO，实体，又有不同

    }

    @ApiOperation("员工新增接口")
    @PostMapping
    public Result add(@RequestBody EmployeeDTO employeeDTO){
        log.info("开始执行员工新增接口：{}",employeeDTO);
        employeeService.add(employeeDTO);
        return Result.success();
    }

    @ApiOperation("员工分页接口")
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("开始执行员工分页接口:{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.page(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("员工启用禁用接口")
    @PostMapping("/status/{status}")
    public Result status(@PathVariable Integer status , Long id){
        //通过 @PathVariable 可以将 URL 中占位符参数绑定到控制器处理方法的入参中:URL 中的 {xxx} 占位符可以通过
        //@PathVariable(“xxx”) 绑定到操作方法的入参中。
        //在这里，就是把{status}的变量拿下来。在url中这么写，意在说明这里是可变量，占位符
        log.info("开始执行员工启用禁用接口：status={},id={}",status,id);
        employeeService.status(status,id);
        return Result.success();
    }

    @ApiOperation("修改员工接口")
    @PutMapping
    public Result update(@RequestBody Employee employee){
        log.info("开始执行修改员工接口：{}",employee);
        employeeService.update(employee);
        return Result.success();
    }

}
