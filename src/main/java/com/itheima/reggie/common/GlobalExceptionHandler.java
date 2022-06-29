package com.itheima.reggie.common;

import com.itheima.reggie.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 * 底层是基于代理,代理controller
 */

/**
 * 通知,来指定拦截了哪些controller
 * 这里是拦截了加了RestController,Controller注解的类
 */
@ControllerAdvice(annotations={RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * 在这里抛异常会在这个方法拦截到,统一进行处理
     * 把异常信息注入进去
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        //日志.输出异常信息
        log.error(ex.getMessage());

        /**
         * 判断输出的日志里,有没有包含Duplicate entry关键字
         * 将得到的信息(getMessage())通过空格分隔(这是一个数组对象)
         * 得到重复的字段的数组下标
         * 然后输出错误信息,已经存在
         */
        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    /**
     * 异常处理方法
     * @param ex
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex) {
        //日志.输出异常信息
        log.error(ex.getMessage());


        return R.error(ex.getMessage());
    }

}
