package org.example.ltwaicodemother.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.example.ltwaicodemother.common.BaseResponse;
import org.example.ltwaicodemother.common.ResultUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
     public BaseResponse<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException",e.getMessage());
        return ResultUtils.error(e.getCode(),e.getMessage());
    }



    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse<?> RuntimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException",e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }







}
