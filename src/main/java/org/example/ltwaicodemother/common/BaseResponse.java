package org.example.ltwaicodemother.common;

import lombok.Data;
import org.example.ltwaicodemother.exception.ErrorCode;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private int code;
    private  String message;
    private T data;


    public BaseResponse(int code,String message,T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    public BaseResponse(int code,T data) {
        this(code,"",data);
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(),errorCode.getMessage(),null);
    }
    public BaseResponse(T data) {
        this(ErrorCode.SUCCESS.getCode(),ErrorCode.SUCCESS.getMessage(),data);
    }

}
