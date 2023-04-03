package com.shadougao.email.rule.execute;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;

public interface RuleExecute {
    void execute(Mail mail, ReceiveRule.Execute execute);
}
