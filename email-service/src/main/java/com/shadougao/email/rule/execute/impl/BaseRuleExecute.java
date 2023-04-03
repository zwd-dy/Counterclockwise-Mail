package com.shadougao.email.rule.execute.impl;

import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.rule.execute.RuleExecute;
import com.shadougao.email.service.MailService;

public abstract class BaseRuleExecute implements RuleExecute {
    MailService mailService;


    public BaseRuleExecute() {
        this.mailService = GetBeanUtil.getApplicationContext().getBean(MailService.class);
    }

}
