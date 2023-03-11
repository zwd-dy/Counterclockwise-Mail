package com.github.email.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("t_mail")
public class Mail {

    @Id
    private String id;
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
