package com.shadougao.email.common.result.exception;

import com.shadougao.email.common.result.ResponseCode;
import lombok.Getter;

@Getter
public class Exception extends RuntimeException {

    private final ResponseCode code;
    private final String msg;

    public Exception() {
        this(ResponseCode.ERROR, ResponseCode.ERROR.getMessage());
    }

    public Exception(ResponseCode code) {
        this(code, code.getMessage());
    }

    public Exception(String msg) {
        this(ResponseCode.ERROR, msg);
    }

    public Exception(ResponseCode code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}



