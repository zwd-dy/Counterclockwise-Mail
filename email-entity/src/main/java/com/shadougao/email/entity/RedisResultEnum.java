package com.shadougao.email.entity;

import lombok.Getter;

@Getter
public enum RedisResultEnum {


    // 子节点 -> 主程序
    CONNECT_MAIN(100, "子节点连接主程序"),
    DISCONNECT_MAIN(101, "从主程序断开连接"),

    // 主程序 -> 子节点
    CONNECT_SUCCESS(200,"连接主程序成功");


    private final Integer code;
    private final String msg;

    RedisResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
