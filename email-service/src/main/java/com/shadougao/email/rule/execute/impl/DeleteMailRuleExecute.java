package com.shadougao.email.rule.execute.impl;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.rule.execute.annotation.RuleExecuteType;

import java.util.Collections;

/**
 * 将新邮件删除操作
 */
@RuleExecuteType("delMail")
public class DeleteMailRuleExecute extends BaseRuleExecute{
    @Override
    public void execute(Mail mail, ReceiveRule.Execute execute) {
        mailService.delMail(Collections.singletonList(mail));
    }
}
