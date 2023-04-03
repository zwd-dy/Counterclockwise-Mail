package com.shadougao.email.dao.mongo.impl;

import com.shadougao.email.dao.mongo.ReceiveRuleDao;
import com.shadougao.email.entity.MailTag;
import com.shadougao.email.entity.ReceiveRule;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReceiveRuleDaoImpl extends BaseDaoImpl<ReceiveRule> implements ReceiveRuleDao {
    public ReceiveRuleDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, ReceiveRule.class, "t_receive_rule");
    }

}
