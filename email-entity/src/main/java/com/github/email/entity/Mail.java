package com.github.email.entity;

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
    private String recipient;
    /**
     * 附件id
     */
    private String fileId;

}
