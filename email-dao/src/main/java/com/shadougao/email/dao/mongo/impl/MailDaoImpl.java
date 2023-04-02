package com.shadougao.email.dao.mongo.impl;

import cn.hutool.core.util.ReflectUtil;
import com.shadougao.email.annotation.MongoLikeQuery;
import com.shadougao.email.dao.mongo.MailDao;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.dto.PageData;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

@Component
public class MailDaoImpl extends BaseDaoImpl<Mail> implements MailDao {

    public MailDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, Mail.class, "t_mail");
    }

    @Override
    public List<Mail> findByUidAndBindId(List<String> uid, String bindId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("uid").in(uid).and("bindId").is(bindId));
        return this.find(query);
    }

    @Override
    public PageData<Mail> pageList(PageData pageData, Mail entity) {
        Integer pageNum = pageData.getPageNum();
        Integer pageSize = pageData.getPageSize();

        Query query = new Query();
        String sortTimeField = "receiveTime";
        if (entity.getType() == 0) {
            sortTimeField = "sendTime";
        } else if (entity.getType() == 2 || entity.getType() == 3) { // 草稿箱 or 定时发送
            sortTimeField = "_id";
        }
        query.with(Sort.by(Sort.Direction.DESC, sortTimeField));


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
                    if(field.getType().isArray()){
                        criteria = criteria.and(field.getName()).in((String[])fieldValue);

                    } else if(field.isAnnotationPresent(MongoLikeQuery.class)){
                        // 是否模糊查询
                        criteria = criteria.and(field.getName()).regex((String) fieldValue);
                    }
                    else {
                        criteria = criteria.and(field.getName()).is(fieldValue);
                    }
                }
            }
            query.addCriteria(criteria);
        }
        // 设置总数量
        long count = this.getMongoTemplate().count(query, this.getCollectionName());
        pageData.setTotalNum(count);
        if (count <= 0) {
            return pageData;
        }
        // 设置总页数
        pageData.setTotalPages((pageData.getTotalNum().intValue() + pageSize - 1) / pageSize);

        if (pageNum != 1) {
            // 查上一页
            int number = (pageNum - 1) * pageSize;
            query.limit(number);
            List<Mail> mails = this.getMongoTemplate().find(query, entityClass);

            // 取出最后一条
            Mail data = mails.get(mails.size() - 1);
            // 取到上一页的最后一条收件时间
            Long sortTime = data.getReceiveTime();
            String id = null;
            if (entity.getType() == 0) {
                sortTime = data.getSendTime();
            } else if (entity.getType() == 2 || entity.getType() == 3) {
                id = data.getId();
            }
            // 从上一条最后一条开始查
            query.addCriteria(Criteria.where(sortTimeField).lt(id != null ? id : sortTime));
        }

        query.limit(pageSize);
        List<Mail> dataList = this.getMongoTemplate().find(query, entityClass);
        pageData.setPageData(dataList);

        return pageData;
    }


}
