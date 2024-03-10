package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.crypto.spec.PSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    //注入盐
    @Value("${sky.md5.salt}")
    private String salt;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // password: 是前端传入过来明文的密码
        // employee.getPassword() : 是从数据库获取的密文密码
        // 对比方案： 明文加盐加密得到密文，再与数据库密文对比
        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 员工新增方法
     *
     * @param employeeDTO
     */
    @Override
    public void add(EmployeeDTO employeeDTO) {

        //1.创建Employee对象
        Employee employee = new Employee();

        //2.将employeeDTO数据赋值给employee对象
        //对象之间赋值语法：BeanUtils.copyProperties(source,target);
        //               会将source与target相同的属性名，将source属性值赋值给target
        BeanUtils.copyProperties(employeeDTO,employee);

        //3.补全数据
        //密码：默认 123456，加盐加密
        employee.setPassword(DigestUtils.md5DigestAsHex((
                PasswordConstant.DEFAULT_PASSWORD+salt).getBytes()));
        //状态：默认 1
        employee.setStatus(StatusConstant.ENABLE);
        // //创建时间：当前时间
        // employee.setCreateTime(LocalDateTime.now());
        // //修改时间：当前时间
        // employee.setUpdateTime(LocalDateTime.now());
        /*
        * 动态获取登录人id:
        *   方案1：解析jwt令牌
        *         这个方案不好，需要重复解析jwt，性能低
        *   方案2：ThreadLocal 【推荐】
        *         在拦截器解析第一次jwt令牌时获取的登录人id存储下来，之后在这里获取出来使用
        *         没有重复解析jwt令牌，性能优秀
        * */
        //从当前线程ThreadLocal里面获取登录人id
        // Long empId = BaseContext.getCurrentId();
        //创建人
        // employee.setCreateUser(empId);
        // //修改人
        // employee.setUpdateUser(empId);

        //打印线程id
        log.info("JwtTokenAdminInterceptor线程id:{}",Thread.currentThread().getId());


        //4.调用mapper插入员工数据到数据库
        employeeMapper.insert(employee);

    }

    /**
     * 员工分页查询数据
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult page(EmployeePageQueryDTO employeePageQueryDTO) {
        //1.设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(),
                employeePageQueryDTO.getPageSize());

        //2.执行查询sql语句
        //分析：select * from employee where name like concat('%',#{name},'%')
        List<Employee> employeeList = employeeMapper.page(employeePageQueryDTO.getName());
        Page<Employee> page = (Page<Employee>) employeeList;

        //3.封装PageResult返回
        //return new PageResult(page.getTotal(),page.getResult());
        // 使用建造者设计模式创建对象 ，第一步：PageResult类上添加@Builder
        //第二步：使用创建
        return PageResult.builder()
                .total(page.getTotal())
                .records(page.getResult())
                .build();
    }

    /**
     * 修改员工状态
     *
     * @param status
     * @param id
     */
    @Override
    public void status(Integer status, Long id) {
        //分析sql语句：update employee set status=#{status},update_time=#{updateTime},
        //              update_user=#{updateUser} where id=#{id}

        //1.将修改的数据封装到Employee对象中
        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                // .updateTime(LocalDateTime.now())
                // .updateUser(BaseContext.getCurrentId())
                .build();

        //2.执行sql语句修改
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @Override
    public Employee findById(Long id) {
        //sql分析: select  * from employee where id = #{id}
        return employeeMapper.findById(id);
    }

    /**
     * 修改员工
     *
     * @param employee
     */
    @Override
    public void update(Employee employee) {
        //补全数据
        //修改时间
        // employee.setUpdateTime(LocalDateTime.now());
        // //修改人
        // employee.setUpdateUser(BaseContext.getCurrentId());

        //执行修改
        employeeMapper.update(employee);
    }
}
