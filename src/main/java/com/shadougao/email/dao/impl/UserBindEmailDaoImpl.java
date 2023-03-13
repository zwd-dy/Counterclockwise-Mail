package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.UserBindEmailDao;
import com.shadougao.email.entity.UserBindEmail;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserBindEmailDaoImpl extends BaseDaoImpl<UserBindEmail> implements UserBindEmailDao {
    public UserBindEmailDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, UserBindEmail.class);
    }
}
