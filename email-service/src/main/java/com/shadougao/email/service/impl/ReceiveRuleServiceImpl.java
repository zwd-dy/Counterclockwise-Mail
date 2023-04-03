package com.shadougao.email.service.impl;

import com.shadougao.email.dao.mongo.ReceiveRuleDao;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.ReceiveRule;
import com.shadougao.email.rule.ReceiveRuleExecutor;
import com.shadougao.email.service.ReceiveRuleService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReceiveRuleServiceImpl extends ServiceImpl<ReceiveRuleDao, ReceiveRule> implements ReceiveRuleService {
    @Override
    @Async
    public void executeRule(List<Mail> mailList) {
        ReceiveRuleExecutor ruleExecutor = new ReceiveRuleExecutor();

        for (int i = 0; i < mailList.size(); i++) {
            Mail mail = mailList.get(i);
            ruleExecutor.setMail(mail);
            List<ReceiveRule> receiveRules = this.getBaseMapper().find(new Query().addCriteria(Criteria.where("userId").is(mail.getUserId())));
            for (int j = 0; j < receiveRules.size(); j++) {
                // 判断规则是否开启
                if (receiveRules.get(j).getIsOpen() == 0) {
                    continue;
                }
                List<ReceiveRule.Condition> conditions = receiveRules.get(j).getConditions();
                List<ReceiveRule.Execute> executes = receiveRules.get(j).getExecutes();
                ruleExecutor.execute(conditions, executes);
            }
        }
    }
}
