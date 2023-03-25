package com.shadougao.email.common.result.exception;

import com.shadougao.email.common.result.ResponseCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 统一异常处理
 */
@Getter
public class BadRequestException extends Exception {

    private Integer status = HttpStatus.BAD_REQUEST.value();

    public BadRequestException() {
        this(ResponseCode.BAD_REQUEST, ResponseCode.BAD_REQUEST.getMessage());
    }

    public BadRequestException(String msg){
        super(msg);
    }

    public BadRequestException(HttpStatus status, String msg){
        super(msg);
        this.status = status.value();
    }

    public BadRequestException(ResponseCode code) {
        this(code, code.getMessage());
    }

    public BadRequestException(ResponseCode code, String message) {
        super(code, message);
    }
}
