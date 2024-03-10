package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * @Description 公共字段填充切面类
 * @Author songyu
 * @Date 2023-09-24
 */
@Component
@Aspect
@Slf4j
public class AutoFillAspect {

    /**
     * 扫描mapper包下所有类，类中带有注解@AutoFill的所有方法
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pt(){}

    /**
     * 前置通知：扫描对应的方法，将方法参数进行公共字段填充
     * @param jp
     */
    @Before("pt()")
    public void before(JoinPoint jp) throws Exception {
        log.info("开始进行公共字段填充。。。");

        //1.获取原有方法上注解AutoFill对象，就得到操作类型
        //得到原有方法签名
        MethodSignature signature = (MethodSignature) jp.getSignature();
        //得到原有方法上注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        //2.获取原有方法参数对象
        Object[] args = jp.getArgs();
        if(args==null && args.length==0){
            return; //没有天数直接返回
        }
        Object obj = args[0];

        //3.进行公共字段填充
        //获取登录人id和系统当前时间
        Long empId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        //必须填充的数据：更新人，更新时间
        // obj: 对象.setUpdateUser(登录人id)
        // obj: 对象.setUpdateTime(当前系统时间)
        Class clazz = obj.getClass();
        //反射获取指定方法语法：clazz.getMethod(方法名,参数类型列表)
        Method setUpdateTimeMethod = clazz.getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUserMethod = clazz.getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
        //执行方法，语法：setUpdateTimeMethod.invoke(方法所属对象,参数值)
        setUpdateUserMethod.invoke(obj,empId);
        setUpdateTimeMethod.invoke(obj,now);

        //只有insert操作才填充的数据：创建人，创建时间
        if(operationType == OperationType.INSERT){
            Method setCreateTimeMethod = clazz.getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUserMethod = clazz.getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            setCreateUserMethod.invoke(obj,empId);
            setCreateTimeMethod.invoke(obj,now);
        }
    }
}
