package com.own.handler;

import com.own.constant.MessageConstant;
import com.own.exception.BaseException;
import com.own.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

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
        log.error("异常信息：{}",ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result doSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.error("异常信息：{}",message);
        if(message.contains("Duplicate entry")){
            String[] splits = message.split(" ");
            return Result.error("【"+splits[2]+"】已存在");
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }
}
