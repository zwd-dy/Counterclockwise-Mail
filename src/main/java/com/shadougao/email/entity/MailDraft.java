package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 草稿邮件，待发送
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_mail_draft")
public class MailDraft extends BaseEntity {

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
    private String fileId;
    /**
     * 用户id
     */
    private String userId;

}
