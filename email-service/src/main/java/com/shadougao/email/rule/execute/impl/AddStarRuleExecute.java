package com.shadougao.email.rule.execute.impl;

import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.rule.execute.annotation.RuleExecuteType;

import java.util.Collections;

/**
 * 将新邮件加星标操作
 */
@RuleExecuteType("AddStar")
public class AddStarRuleExecute extends BaseRuleExecute {
    @Override
    public void execute(Mail mail, ReceiveRule.Execute execute) {
        mailService.updateStar(Collections.singletonList(mail), MailEnum.STAR_YES);
    }
}
