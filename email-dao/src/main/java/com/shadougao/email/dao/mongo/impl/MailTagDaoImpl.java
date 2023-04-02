package com.shadougao.email.dao.mongo.impl;

import com.shadougao.email.dao.mongo.MailTagDao;
import com.shadougao.email.entity.MailFile;
import com.shadougao.email.entity.MailTag;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MailTagDaoImpl extends BaseDaoImpl<MailTag> implements MailTagDao {
    public MailTagDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, MailTag.class, "t_mail_tag");
    }
}
