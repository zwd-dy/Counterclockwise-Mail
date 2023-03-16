package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.AddressBookGroupDao;
import com.shadougao.email.dao.BaseDao;
import com.shadougao.email.entity.AddressBookGroup;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddressBookGroupDaoImpl extends BaseDaoImpl<AddressBookGroup> implements AddressBookGroupDao {
    public AddressBookGroupDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AddressBookGroup.class, "t_address_book_group");
    }
}
