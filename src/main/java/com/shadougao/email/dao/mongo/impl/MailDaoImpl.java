package com.shadougao.email.dao.mongo.impl;

import com.shadougao.email.dao.mongo.MailDao;
import com.shadougao.email.entity.Mail;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MailDaoImpl extends BaseDaoImpl<Mail> implements MailDao {

    public MailDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Mail.class, "t_mail");
    }
}
