package com.shadougao.email.entity;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RedisResult implements Serializable {

    @Serial
    private static final long serialVersionUID = -489365356544203557L;

    private Integer code;
    private Object data;

    public RedisResult(RedisResultEnum code, Object data) {
        this.code = code.getCode();
        this.data = data;
    }


}
