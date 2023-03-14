package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.BaseDao;
import com.shadougao.email.entity.BaseEntity;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.dto.PageData;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@RequiredArgsConstructor
public class BaseDaoImpl<T extends BaseEntity> implements BaseDao<T> {

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
    public PageData<T> pageList(PageData pageData) {
        Integer pageNum = pageData.getPageNum();
        Integer pageSize = pageData.getPageSize();

        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, "_id"));

        // TODO 条件查询

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
            // 取到上一页的最后一条数据 id，当作条件查接下来的数据
            String id = data.getId();
            // 从上一页最后一条开始查（大于不包括这一条）
            query.addCriteria(Criteria.where("_id").gt(new ObjectId(id)));
        }

        query.limit(pageSize);
        List<T> dataList = mongoTemplate.find(query, entityClass);
        pageData.setPageData(dataList);

        return pageData;
    }

}
