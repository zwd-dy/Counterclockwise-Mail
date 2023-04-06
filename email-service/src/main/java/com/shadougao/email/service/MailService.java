package com.shadougao.email.service;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.Mail;

import java.util.List;

public interface MailService extends IService<Mail> {

    /**
     * 用户发送邮件
     * @param mail 邮件
     * @return /
     */
    void sendMail(Mail mail);

    void delMail(List<Mail> mailList);

    Result<?> saveDraft(Mail mail);

    Result<?> schedule(Mail mail);

    void delScheduleMail(List<Mail> mailList);

    void updateSchedule(Mail mail);

    void addToTag(List<Mail> mailList, List<String> tagIds);

    void delToTag(List<Mail> mailList);

    void updateStar(List<Mail> mailList,Integer isStar);

}
