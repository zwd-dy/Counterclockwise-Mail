package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.MailDraftDao;
import com.shadougao.email.entity.MailDraft;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MailDraftDaoImpl extends BaseDaoImpl<MailDraft> implements MailDraftDao {
    public MailDraftDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, MailDraft.class, "t_mail_draft");
    }
}
