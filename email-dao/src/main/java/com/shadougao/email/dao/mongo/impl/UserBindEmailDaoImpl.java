package com.shadougao.email.dao.mongo.impl;

import com.shadougao.email.dao.mongo.UserBindEmailDao;
import com.shadougao.email.entity.UserBindEmail;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserBindEmailDaoImpl extends BaseDaoImpl<UserBindEmail> implements UserBindEmailDao {
    public UserBindEmailDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, UserBindEmail.class,"t_user_bind_email");
    }


    @Override
    public UserBindEmail getByEmailUser(Long userId,String emailUser) {
        Query query = new Query();
        query.addCriteria(Criteria.where("emailUser").is(emailUser).and("userId").is(userId));
       return this.findOne(query);
    }

    @Override
    public List<UserBindEmail> emailBindList(Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where(("userId")).is(userId));
        return this.find(query);
    }
}
