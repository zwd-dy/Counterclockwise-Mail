package com.shadougao.email.common.result;

public class MailEnum {


    public static int TYPE_SENT = 0;    // 邮箱类型：已发送
    public static int TYPE_RECEIVE = 1; // 邮箱类型：收件箱
    public static int TYPE_DRAFT = 2;   // 邮箱类型：草稿箱
    public static int TYPE_SCHEDULE = 3; // 邮箱类型：定时发送

    public static int SEND_SUCCESS = 0; // 发送状态：成功
    public static int SEND_ING = 1;     // 发送状态：正在发送
    public static int SEND_ERROR = 2;   // 发送状态：失败

    public static int STAR_NO = 0;      // 是星标邮件
    public static int STAR_YES = 1;     // 不是星标邮件

}
