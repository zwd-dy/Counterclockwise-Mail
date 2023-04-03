package com.shadougao.email.service;

import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;

import java.util.List;

public interface ReceiveRuleService extends IService<ReceiveRule> {
    void executeRule(List<Mail> mailList);
}
