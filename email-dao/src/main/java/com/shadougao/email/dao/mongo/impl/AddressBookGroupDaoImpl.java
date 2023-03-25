package com.shadougao.email.dao.mongo.impl;

import com.shadougao.email.dao.mongo.AddressBookGroupDao;
import com.shadougao.email.entity.AddressBookGroup;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddressBookGroupDaoImpl extends BaseDaoImpl<AddressBookGroup> implements AddressBookGroupDao {

    public AddressBookGroupDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AddressBookGroup.class, "t_address_book_group");
    }

}
