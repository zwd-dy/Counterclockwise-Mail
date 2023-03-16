package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_mail")
public class Mail extends BaseEntity {

    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件正文
     */
    private String content;
    /**
     * 发件人地址
     */
    private String from;
    /**
     * 收件人地址
     */
    private String[] recipients;
    /**
     * 附件id
     */
    private String[] fileId;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 1.已发送    2.收件箱   3.草稿箱
     */
    private int type;
    /**
     * 收件时间
     */
    private String receiveTime;
    /**
     * 发件时间
     */
    private String sendTime;

}
