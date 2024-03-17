package com.own.aspect;

import com.own.annotation.AutoFill;
import com.own.constant.AutoFillConstant;
import com.own.context.BaseContext;
import com.own.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component
@Aspect
@Slf4j
public class AutoFillAspect {

    //扫描mapper包下所有类，类中带有注解@AutoFill的所有方法
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pt(){}

    //前置通知：扫描对应的方法，将方法参数进行公共字段填充
    @Before("pt()")
    public void before(JoinPoint jp) throws Exception{
        log.info("开始进行公共字段填充");

        //获取原有方法上注解AutoFill对象，就得到操作类型
        //得到原有方法签名
        MethodSignature signature = (MethodSignature) jp.getSignature();

        //得到原有方法上注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        //获取原有方法参数对象
        Object[] args = jp.getArgs();
        Object obj = args[0];

        //进行公共字段填充
        //获取登录人id和系统当前时间
        Long empId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        //必须填充的数据：更新人，更新时间
        Class clazz = obj.getClass();
        Method setUpdateTimeMethod = clazz.getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
        Method setUpdateUserMethod = clazz.getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

        setUpdateUserMethod.invoke(obj,empId);
        setUpdateTimeMethod.invoke(obj,now);

        //只有insert操作才填充的数据：创建人，创建时间
        if(operationType == OperationType.INSERT){
            Method setCreateTimeMethod = clazz.getMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
            Method setCreateUserMethod = clazz.getMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
            setCreateUserMethod.invoke(obj,empId);
            setCreateTimeMethod.invoke(obj,now);
        }

    }

}
