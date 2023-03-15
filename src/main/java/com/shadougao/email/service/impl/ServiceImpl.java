package com.shadougao.email.service.impl;

import com.shadougao.email.dao.BaseDao;
import com.shadougao.email.entity.BaseEntity;
import com.shadougao.email.entity.dto.PageData;
import com.shadougao.email.service.IService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ServiceImpl<M extends BaseDao<T>,T extends BaseEntity> implements IService<T> {

    @Autowired
    protected M baseMapper;

    @Override
    public T getOneById(String id) {
        return baseMapper.getOneById(id);
    }

    @Override
    public List<T> getByIds(List<String> ids) {
        return baseMapper.getByIds(ids);
    }

    @Override
    public List<T> getAll() {
        return baseMapper.getAll();
    }

    @Override
    public void delOne(String id) {
        baseMapper.delOne(id);
    }

    @Override
    public void batchDel(List<String> names) {
        baseMapper.batchDel(names);
    }

    @Override
    public T addOne(T t) {
        return baseMapper.addOne(t);
    }

    @Override
    public T updateOne(T t, String id) {
        return baseMapper.updateOne(t,id);
    }

    @Override
    public BaseDao<T> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public PageData<T> pageList(PageData pageData) {
        return baseMapper.pageList(pageData);
    }
}