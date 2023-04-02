package com.shadougao.email.entity;

import com.shadougao.email.annotation.MongoLikeQuery;
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
public class Mail extends MongoBaseEntity {

    /**
     * 邮箱UID
     */
    private String uid;
    /**
     * 邮件主题
     */
    @MongoLikeQuery
    private String subject;
    /**
     * 邮件正文
     */
    @MongoLikeQuery
    private String content;
    /**
     * 发件人地址
     */
    private String from;
    /**
     * 发件人姓名
     */
    private String formName;
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
    private Long userId;
    /**
     * 0.已发送    1.收件箱   2.草稿箱   3.定时发送
     */
    private Integer type;
    /**
     * 发送状态
     *  0.已投递到对方邮箱  1.正在发送  2.发送失败
     */
    private Integer sendState;
    /**
     * 异常日志
     */
    private String sendExceptionLog;
    /**
     * 收件异常日志
     */
    private String receiveExceptionLog;
    /**
     * 收件时间
     */
    private Long receiveTime;
    /**
     * 发件时间
     */
    private Long sendTime;
    /**
     * 收件的绑定邮箱
     */
    private String bindId;
    /**
     * 标签ID
     */
    private String[] tagIds;
    /**
     * 是否星标,    0:不是    1/是
     */
    private Integer isStar;

}
