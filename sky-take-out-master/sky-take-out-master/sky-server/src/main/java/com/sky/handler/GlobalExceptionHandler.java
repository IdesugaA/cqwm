package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 目标：统一处理苍穹外卖所有表的唯一约束异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result doSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.error("异常信息：{}",message);
        //判断是否是唯一约束异常
        if(message.contains("Duplicate entry")){
            //说明是唯一约束异常： 提示用户“xxxx已存在”
            //对错误信息字符串切换，获取里面的itheima2
            String[] splits = message.split(" ");
            return Result.error("【"+splits[2]+"】已存在");
        }else{
            //不是唯一约束异常：提示用户”未知错误“
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
