package com.shadougao.email.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    PAYMENT_REQUIRED(402, "Payment Required"),
    RESOURCE_NOT_FOUND(404, "资源不存在"),

    BAD_REQUEST(400, "响应失败"),
    ERROR(40001, "未知错误"),
    VALIDATE_FAILED(40002, "参数校验失败"),
    RESOURCE_ALREADY_EXIST(40003, "资源已存在"),

    FILE_NOT_VALID(40010, "文件格式错误"),
    MAIL_ERROR(40020, "邮件发送错误"),

    USER_NOT_LOGIN(40004, "用户未登陆"),
    USER_NOT_AUTH(40005, "用户无权限"),
    USER_NOT_REGISTER(40006, "用户未注册"),

    REPEAT_SUBMIT(40007, "请勿重复提交"),

    INTERNAL_SERVER_ERROR(500, "服务器出错"),


    SUCCESS(0, "请求成功"),
    RESOURCE_CREATED(20001, "资源创建成功"),

    FAIL(-1, "请求失败");


    private final int code;
    private final String message;
}
