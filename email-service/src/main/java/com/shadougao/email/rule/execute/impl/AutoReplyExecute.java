package com.shadougao.email.rule.execute.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.execute.MailExecutor;
import com.shadougao.email.execute.SendMailExecute;
import com.shadougao.email.rule.execute.annotation.RuleExecuteType;
import com.shadougao.email.service.SysEmailPlatformService;
import com.shadougao.email.service.UserBindEmailService;

import java.util.Map;

/**
 * 自动回复新邮件
 */
@RuleExecuteType("autoReply")
public class AutoReplyExecute extends BaseRuleExecute{
    @Override
    public void execute(Mail mail, ReceiveRule.Execute execute) {
        UserBindEmailService bindEmailService = GetBeanUtil.getApplicationContext().getBean(UserBindEmailService.class);
        SysEmailPlatformService platformService = GetBeanUtil.getApplicationContext().getBean(SysEmailPlatformService.class);
        MailExecutor mailExecutor = GetBeanUtil.getApplicationContext().getBean(MailExecutor.class);

        // 获取发件人
        String from = mail.getFrom();
        // 获取参数
        Map<String,String> map =(Map<String,String>) execute.getParam();
        // 获取发件邮箱
        String bindId = map.get("bindId");
        UserBindEmail bindEmail = bindEmailService.getOneById(bindId);
        // 获取回复内容
        String content = map.get("content");
       // 获取邮箱平台
        SysEmailPlatform platform = platformService.getOneById(bindEmail.getPlatformId());

        Mail replyMail = new Mail();
        replyMail.setSubject("回复：【"+mail.getSubject()+"】");
        replyMail.setFrom(bindEmail.getEmailUser());
        replyMail.setRecipients(from.split(","));
        replyMail.setSendState(MailEnum.SEND_ING);
        replyMail.setType(MailEnum.TYPE_SENT);
        replyMail.setUserId(mail.getUserId());
        replyMail.setSendExceptionLog("null");
        replyMail.setContent(content);
        mailService.addOne(replyMail);
        SendMailExecute sendMailExecute = new SendMailExecute(bindEmail, platform, replyMail);
        mailExecutor.executeSend(sendMailExecute);

    }
}
