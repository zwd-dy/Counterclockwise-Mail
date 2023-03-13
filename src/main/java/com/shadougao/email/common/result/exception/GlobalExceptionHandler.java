package com.shadougao.email.common.result.exception;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.ResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BadCredentialsException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result<?> badCredentialsException(BadCredentialsException e) {
        // 打印堆栈信息
        String message = "坏的凭证".equals(e.getMessage()) ? "用户名或密码不正确" : e.getMessage();
        // todo log
        return buildResponseEntity(ResultEnum.USER_NOT_AUTH, message);
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(value = BadRequestException.class)
    public Result<?> badRequestException(BadRequestException e) {
        // 打印堆栈信息
        log.error(e.getMessage());
        return buildResponseEntity(ResultEnum.BAD_REQUEST, e.getMessage());
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 打印堆栈信息
        log.error(e.getMessage());
        String[] str = Objects.requireNonNull(e.getBindingResult().getAllErrors().get(0).getCodes())[1].split("\\.");
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        String msg = "不能为空";
        if (msg.equals(message)) {
            message = str[1] + ":" + message;
        }
        return buildResponseEntity(ResultEnum.VALIDATE_FAILED, message);
    }

    /**
     * 统一返回
     */
    private Result<?> buildResponseEntity(ResultEnum code, String message) {
        return Result.error(code, message);
    }

}
