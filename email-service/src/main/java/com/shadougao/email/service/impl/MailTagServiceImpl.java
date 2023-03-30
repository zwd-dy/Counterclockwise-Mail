package com.shadougao.email.service.impl;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.exception.BadRequestException;
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
    public Result<?> addTag(MailTag mailTag) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        // 判断标签是否存在
        if (tagDao.findOne(new Query().addCriteria(Criteria.where("label").is(mailTag.getLabel()).and("userId").is(userId))) != null) {
            throw new BadRequestException("标签名已存在");
        }
        // 添加标签
        mailTag.setUserId(userId);
        return Result.success(tagDao.addOne(mailTag));
    }

    @Override
    public Result<?> delTag(String tagId) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        // 判断标签是否存在
        if (tagDao.getOneById(tagId) == null) {
            throw new BadRequestException("标签名不存在");
        }
        // 删除标签
        tagDao.delOne(tagId);
        return Result.success();
    }

    @Override
    public Result<?> getTag() {
        List<MailTag> tagData = new ArrayList<>();
        Long userId = SecurityUtils.getCurrentUser().getId();
        // 取出所有父级
        List<MailTag> parentTags = tagDao.find(new Query().addCriteria(Criteria.where("userId").is(userId).and("parentId").is("0")));

        for (int i = 0; i < parentTags.size(); i++) {
            tagData.add(findChild(parentTags.get(i).getId()));
        }

        return Result.success(tagData);
    }


    /**
     * 递归算法-算出子节点
     */
    public MailTag findChild(String orgId) {
        List<MailTag> childList = new ArrayList<>();
        // 通过id获取所有VO信息
        MailTag organizationVO = tagDao.getOneById(orgId);

        //查找children子节点，递归程序必须要有一个出口
        List<MailTag> organizationList = this.tagDao.find(new Query().addCriteria(Criteria.where("parentId").is(orgId)));
        //organizationVO.setChild(organizationList);
        for (MailTag item : organizationList) {
            childList.add(findChild(item.getId()));
        }
        organizationVO.setChildren(childList);
        return organizationVO;
    }


}
