package com.xjx.mydns.exception;

import com.xjx.mydns.constant.ResultObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
@Slf4j
public class ExceptionResolver {

    @ExceptionHandler(value = MyException.class)
    public ResultObj dealWithMyException(MyException e){
          log.error("出现了自定义异常,异常信息为: {} --- 异常码为: {}",e.getMsg(),e.getCode());
          return ResultObj.failure(e.getMsg());
    }

    @ExceptionHandler(value = Exception.class)
    public ResultObj dealWithOtherException(Exception e){
          log.error("出现了其余异常,异常信息为: {}",e.getMessage());
          return ResultObj.failure(e.getMessage());
    }
}
