package com.shadougao.email.rule.execute.impl;


import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.rule.execute.annotation.RuleExecuteType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 将新邮件添加到标签操作
 */
@RuleExecuteType("addTag")
public class AddTagRuleExecute extends BaseRuleExecute {
    @Override
    public void execute(Mail mail, ReceiveRule.Execute execute) {
        String[] tagIds = ((String) execute.getParam()).split(",");
        mailService.addToTag(Collections.singletonList(mail), Arrays.asList(tagIds));
    }

}
