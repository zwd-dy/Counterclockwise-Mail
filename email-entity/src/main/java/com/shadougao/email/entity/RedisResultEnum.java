package com.shadougao.email.entity;

import lombok.Getter;

@Getter
public enum RedisResultEnum {


    // 子节点 -> 主程序
    HEART_BEAT(110,"心跳包"),
    CONNECT_MAIN(100, "子节点连接主程序"),
    DISCONNECT_MAIN(101, "从主程序断开连接"),
    NEW_MAIL_UIDS(102,"新邮件通知"),

    // 主程序 -> 子节点
    CONNECT_SUCCESS(200,"连接主程序成功"),
    TASK_RETRIEVE(201,"重新获取任务"),
    TASK_NEW_ADD(202,"新任务加入"),
    TASK_DEL(203,"删除任务"),
    TASK_UPDATE(203,"更新任务");


    private final Integer code;
    private final String msg;

    RedisResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
