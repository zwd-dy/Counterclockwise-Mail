package com.shadougao.email.dao.mongo.impl;

import com.shadougao.email.dao.mongo.AddressBookDao;
import com.shadougao.email.entity.AddressBook;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class AddressBookDaoImpl extends BaseDaoImpl<AddressBook> implements AddressBookDao {
    public AddressBookDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AddressBook.class, "t_address_book");
    }
}
