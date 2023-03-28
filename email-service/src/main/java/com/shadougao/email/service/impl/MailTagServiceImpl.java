package com.shadougao.email.service.impl;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.dao.mongo.MailTagDao;
import com.shadougao.email.entity.MailTag;
import com.shadougao.email.service.MailTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailTagServiceImpl extends ServiceImpl<MailTagDao, MailTag> implements MailTagService {
    private final MailTagDao tagDao;

    @Override
    public Result<?> addTag(MailTag mailTag, String parentId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        MailTag tag = tagDao.findOne(new Query().addCriteria(Criteria.where("userId").is(userId)));
        addTagByParentId(tag, parentId, mailTag);
        tagDao.updateOne(tag);
        return Result.success();
    }

    @Override
    public Result<?> delTag(MailTag mailTag) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        String delId = mailTag.getId();
        MailTag tag = tagDao.findOne(new Query().addCriteria(Criteria.where("userId").is(userId)));
        if(tag.getId().equals(delId)){
            tagDao.delOne(delId);
        }else {
            delTagByParentId(tag,delId);
            tagDao.updateOne(tag);
        }
        return Result.success() ;
    }

    public void addTagByParentId(MailTag tag, String parentId, MailTag newTag) {
        if ("0".equals(parentId) || tag == null) {
            tag = newTag;
            return;
        }
        List<MailTag> tags = tag.getChildren();
        if (tags != null) {
            for (int i = 0; i < tags.size(); i++) {
                MailTag parent = tags.get(i);
                if (parentId.equals(parent.getId())) {
                    List<MailTag> children = parent.getChildren();
                    if (children == null) children = new ArrayList<>();
                    children.add(newTag);
                    parent.setChildren(children);
                    return;
                } else {
                    addTagByParentId(parent, parentId, newTag);
                }
            }
        }
    }

    public void delTagByParentId(MailTag tag, String id) {
        List<MailTag> tags = tag.getChildren();
        if (tags != null) {
            for (int i = 0; i < tags.size(); i++) {
                MailTag tag1 = tags.get(i);
                if (id.equals(tag1.getId())) {
                    tags.remove(tag1);
                    return;
                } else {
                    delTagByParentId(tag1, id);
                }
            }
        }
    }
}
