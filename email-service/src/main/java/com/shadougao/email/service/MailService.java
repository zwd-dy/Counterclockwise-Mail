package com.shadougao.email.service;

import com.shadougao.email.entity.Mail;

public interface MailService extends IService<Mail> {

    /**
     * 用户发送邮件
     * @param mail
     * @return
     */
    void sendMail(Mail mail);
}
