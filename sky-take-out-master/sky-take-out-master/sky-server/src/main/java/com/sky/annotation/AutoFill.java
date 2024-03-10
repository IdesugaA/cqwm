package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description 标识方法进行公共字段填充的注解
 * @Author songyu
 * @Date 2023-09-24
 */
@Target(ElementType.METHOD) //设置注解写在方法上
@Retention(RetentionPolicy.RUNTIME)  //设置注解在运行时可用
public @interface AutoFill {

    OperationType value(); //设置操作类型属性，用于区别标识方法的操作类型


}
