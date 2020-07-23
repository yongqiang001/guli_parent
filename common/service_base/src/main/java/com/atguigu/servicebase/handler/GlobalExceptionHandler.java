package com.atguigu.servicebase.handler;

import com.atguigu.commonutils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){
        e.printStackTrace();
        log.error(e.getMessage());
        return R.error();

    }

    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R error(GuliException e){
    e.printStackTrace();
    log.error(e.getMessage());
    return R.error().message(e.getMsg()).code(e.getCode());

    }
}
