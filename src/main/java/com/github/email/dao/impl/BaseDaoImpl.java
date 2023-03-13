package com.github.email.dao.impl;

import com.github.email.dao.BaseDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class BaseDaoImpl<T> implements BaseDao<T> {

    private final MongoTemplate mongoTemplate;

    protected Class<T> entityClass;

    public BaseDaoImpl(MongoTemplate mongoTemplate, Class<T> entityClass) {
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
    }

    @Override
    public T getOneById(String id, String documentName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, entityClass, documentName);
    }

    @Override
    public List<T> getByIds(List<String> ids, String documentName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, entityClass, documentName);
    }

    @Override
    public List<T> getAll(String documentName) {
        return mongoTemplate.findAll(entityClass, documentName);
    }

    @Override
    public void delOne(String id, String documentName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, entityClass, documentName);
    }

    @Override
    public void batchDel(List<String> ids, String documentName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query, entityClass, documentName);
    }

    @Override
    public T addOne(T t, String documentName) {
        return mongoTemplate.insert(t, documentName);
    }

    @Override
    public T updateOne(T t, String id, String documentName) {
        T t1 = getOneById(id, documentName);
        if (t1 != null) {
            return mongoTemplate.save(t, documentName);
        }
        return null;
    }
}
