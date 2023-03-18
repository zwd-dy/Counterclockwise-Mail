package com.shadougao.email.dao;


import com.shadougao.email.entity.BaseEntity;
import com.shadougao.email.entity.dto.PageData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface BaseDao<T extends BaseEntity> {

    T getOneById(String id);

    List<T> getByIds(List<String> ids);

    List<T> getAll();

    void delOne(String id);

    void batchDel(List<String> names);

    T addOne(T t);

    T updateOne(T t, String id);

    /**
     * 只更新有值的属性
     * @param t
     * @return
     */
    long updateOne(T t);

    T findOne(Query query);

    List<T> find(Query query);

    PageData<T> pageList(PageData pageData,T entity);

    MongoTemplate getMongoTemplate();

}
