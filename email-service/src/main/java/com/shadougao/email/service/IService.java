package com.shadougao.email.service;

import com.shadougao.email.dao.BaseDao;
import com.shadougao.email.entity.BaseEntity;
import com.shadougao.email.entity.dto.PageData;

import java.util.List;

public interface IService<T extends BaseEntity> {

    T getOneById(String id);

    List<T> getByIds(List<String> ids);

    List<T> getAll();

    void delOne(String id);

    void batchDel(List<String> names);

    T addOne(T t);

    T updateOne(T t, String id);

    long updateOne(T t);

    BaseDao<T> getBaseMapper();

    PageData<T> pageList(PageData pageData,T entity);

}
