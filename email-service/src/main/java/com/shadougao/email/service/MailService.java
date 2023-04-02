package com.shadougao.email.service;

import com.shadougao.email.entity.Mail;

import java.util.List;

public interface MailService extends IService<Mail> {

    /**
     * 用户发送邮件
     * @param mail
     * @return
     */
    void sendMail(Mail mail);

    void delMail(List<Mail> mailList);

}
