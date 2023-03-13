package com.github.email.dao;

import java.util.List;

public interface BaseDao<T> {

    T getOneById(String id, String documentName);

    List<T> getByIds(List<String> ids, String documentName);

    List<T> getAll(String documentName);

    void delOne(String id, String documentName);

    void batchDel(List<String> names, String documentName);

    T addOne(T t, String documentName);

    T updateOne(T t, String id, String documentName);

}
