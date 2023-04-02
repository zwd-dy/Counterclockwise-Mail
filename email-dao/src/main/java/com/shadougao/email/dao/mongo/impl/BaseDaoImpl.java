package com.shadougao.email.dao.mongo.impl;

import cn.hutool.core.util.ReflectUtil;
import com.mongodb.client.result.UpdateResult;
import com.shadougao.email.annotation.MongoLikeQuery;
import com.shadougao.email.dao.mongo.BaseDao;
import com.shadougao.email.entity.MongoBaseEntity;
import com.shadougao.email.entity.dto.PageData;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.Field;
import java.util.List;

@RequiredArgsConstructor
public class BaseDaoImpl<T extends MongoBaseEntity> implements BaseDao<T> {

    private final MongoTemplate mongoTemplate;

    protected Class<T> entityClass;

    private String collectionName;


    public BaseDaoImpl(MongoTemplate mongoTemplate, Class<T> entityClass, String collectionName) {
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
        this.collectionName = collectionName;
    }

    @Override
    public T getOneById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, entityClass, collectionName);
    }

    @Override
    public List<T> getByIds(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query, entityClass, collectionName);
    }

    @Override
    public List<T> getAll() {
        return mongoTemplate.findAll(entityClass, collectionName);
    }

    @Override
    public void delOne(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, entityClass, collectionName);
    }

    @Override
    public void batchDel(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query, entityClass, collectionName);
    }

    @Override
    public T addOne(T t) {
        return mongoTemplate.insert(t, collectionName);
    }

    @Override
    public T updateOne(T t, String id) {
        T t1 = getOneById(id);
        if (t1 != null) {
            return mongoTemplate.save(t, collectionName);
        }
        return null;
    }

    @Override
    public long updateMulti(Query query, Update update) {
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, collectionName);
        return updateResult.getModifiedCount();
    }

    @Override
    public long updateOne(T t) {
        Query query = new Query();
        Update update = new Update();

        // 根据id更新
        query.addCriteria(Criteria.where("_id").is(t.getId()));

        // 只更新有值的属性
        Field[] fields = ReflectUtil.getFields(t.getClass());
        for (Field field : fields) {
            // 获取属性值
            Object fieldValue = ReflectUtil.getFieldValue(t, field);
            if (fieldValue != null) {
                update.set(field.getName(), fieldValue);
            }
        }
        return mongoTemplate.updateFirst(query, update, collectionName).getModifiedCount();
    }

    @Override
    public T findOne(Query query) {
        return mongoTemplate.findOne(query, entityClass, collectionName);
    }

    @Override
    public List<T> find(Query query) {
        return mongoTemplate.find(query, entityClass, collectionName);
    }

    @Override
    public PageData<T> pageList(PageData pageData, T entity) {
        Integer pageNum = pageData.getPageNum();
        Integer pageSize = pageData.getPageSize();

        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "_id"));


        // 条件查询
        if (entity != null) {
            Criteria criteria = new Criteria();
            Field[] fields = ReflectUtil.getFields(entity.getClass());

            for (Field field : fields) {
                // 忽略 create_time和id的条件查询
                if ("create_time".equals(field.getName()) || "id".equals(field.getName())) {
                    continue;
                }
                // 获取属性值
                Object fieldValue = ReflectUtil.getFieldValue(entity, field);
                if (fieldValue != null && fieldValue != "") {
                    // 存在值即条件查询
                    if (field.isAnnotationPresent(MongoLikeQuery.class)) {
                        // 是否模糊查询
                        criteria = criteria.and(field.getName()).regex((String) fieldValue);
                    } else {
                        criteria = criteria.and(field.getName()).is(fieldValue);
                    }
                }
            }
            query.addCriteria(criteria);
        }
        // 设置总数量
        pageData.setTotalNum(mongoTemplate.count(query, collectionName));
        // 设置总页数
        pageData.setTotalPages((pageData.getTotalNum().intValue() + pageSize - 1) / pageSize);

        if (pageNum != 1) {
            // 查上一页
            int number = (pageNum - 1) * pageSize;
            query.limit(number);
            List<T> mails = mongoTemplate.find(query, entityClass);

            // 取出最后一条
            T data = mails.get(mails.size() - 1);
            // 取到上一页的最后一条id
            String id = data.getId();
            // 从上一条最后一条开始查
            query.addCriteria(Criteria.where("_id").gt(id));
        }

        query.limit(pageSize);
        List<T> dataList = mongoTemplate.find(query, entityClass);
        pageData.setPageData(dataList);

        return pageData;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return this.mongoTemplate;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
}
