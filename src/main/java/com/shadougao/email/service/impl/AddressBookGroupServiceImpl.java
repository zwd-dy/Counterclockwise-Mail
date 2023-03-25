package com.shadougao.email.service.impl;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.dao.mongo.AddressBookGroupDao;
import com.shadougao.email.entity.AddressBookGroup;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.service.AddressBookGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AddressBookGroupServiceImpl extends ServiceImpl<AddressBookGroupDao, AddressBookGroup> implements AddressBookGroupService {

    private final AddressBookGroupDao groupDao;

    @Override
    public Result addGroup(AddressBookGroup group) {
        SysUser user = SecurityUtils.getCurrentUser();

        // 查询分组是否存在
        Query query = new Query();
        query.addCriteria(Criteria
                .where("userId").is(user.getId())
                .and("name").is(group.getName()));
        AddressBookGroup b = groupDao.findOne(query);

        if(!Objects.isNull(b)){
            throw new BadRequestException("分组名不能重复");
        }

        // 添加分组
        group.setUserId(user.getId());
        return Result.success(this.addOne(group));
    }

    @Override
    public Result delGroup(String id) {
        SysUser user = SecurityUtils.getCurrentUser();

        // 查询分组是否存在
        AddressBookGroup b = groupDao.getOneById(id);
        if(Objects.isNull(b)){
            throw new BadRequestException("该分组不存在");
        }

        groupDao.delOne(id);
        return Result.success();
    }


}
