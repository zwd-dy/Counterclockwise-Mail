package com.shadougao.email.dao;


import com.shadougao.email.entity.BaseEntity;
import com.shadougao.email.entity.dto.PageData;

import java.util.List;

public interface BaseDao<T extends BaseEntity> {

    T getOneById(String id);

    List<T> getByIds(List<String> ids);

    List<T> getAll();

    void delOne(String id);

    void batchDel(List<String> names);

    T addOne(T t);

    T updateOne(T t, String id);

    PageData<T> pageList(PageData pageData);

}
