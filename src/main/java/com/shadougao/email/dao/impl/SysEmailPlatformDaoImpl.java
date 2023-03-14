package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.SysEmailPlatformDao;
import com.shadougao.email.entity.SysEmailPlatform;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class SysEmailPlatformDaoImpl extends BaseDaoImpl<SysEmailPlatform> implements SysEmailPlatformDao {
    public SysEmailPlatformDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, SysEmailPlatform.class,"sys_email_platform");
    }
}
