package com.shadougao.email.service;

import com.shadougao.email.dao.mongo.BaseDao;
import com.shadougao.email.entity.MongoBaseEntity;
import com.shadougao.email.entity.dto.PageData;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface IService<T extends MongoBaseEntity> {

    T getOneById(String id);

    List<T> getByIds(List<String> ids);

    List<T> getAll();

    void delOne(String id);

    void batchDel(List<String> names);
    void batchDel(Query query);

    T addOne(T t);

    T updateOne(T t, String id);

    long updateOne(T t);

    BaseDao<T> getBaseMapper();

    PageData<T> pageList(PageData pageData,T entity);

}
