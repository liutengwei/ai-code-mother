package org.example.ltwaicodemother.common;

import lombok.Data;
import org.example.ltwaicodemother.exception.ErrorCode;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private  String message;
    private T data;


    BaseResponse(int code,String message,T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    BaseResponse(int code,T data) {
        this(code,"",data);
    }

    BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),errorCode.getMessage(),null);
    }



}
