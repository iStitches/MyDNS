package com.xjx.mydns.exception;

import lombok.Data;

@Data
public class MyException extends RuntimeException{
    private String code;
    private String msg;

    public MyException(){
        super();
    }

    public MyException(GlobalException globalException){
        this.code = globalException.getErrorCode();
        this.msg = globalException.getErrorMsg();
    }

    public MyException(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
