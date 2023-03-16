package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.UserBindEmailDao;
import com.shadougao.email.entity.UserBindEmail;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class UserBindEmailDaoImpl extends BaseDaoImpl<UserBindEmail> implements UserBindEmailDao {
    public UserBindEmailDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, UserBindEmail.class,"t_user_bind_email");
    }


    @Override
    public UserBindEmail getByEmailUser(Integer userId,String emailUser) {
        Query query = new Query();
        query.addCriteria(Criteria.where("emailUser").is(emailUser).and("userId").is(userId));
       return this.findOne(query);
    }
}
