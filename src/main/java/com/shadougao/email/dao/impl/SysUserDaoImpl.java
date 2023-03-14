package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.SysUserDao;
import com.shadougao.email.entity.SysUser;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class SysUserDaoImpl extends BaseDaoImpl<SysUser> implements SysUserDao {
    public SysUserDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, SysUser.class, "sys_user");
    }
}
