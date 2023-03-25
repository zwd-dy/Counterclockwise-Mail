package com.shadougao.email.dao.mongo.impl;

import com.shadougao.email.dao.mongo.MailFileDao;
import com.shadougao.email.entity.MailFile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MailFileDaoImpl extends BaseDaoImpl<MailFile> implements MailFileDao {
    public MailFileDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, MailFile.class, "t_file");
    }
}
