package com.shadougao.email.dao.impl;

import com.shadougao.email.dao.AddressBookDao;
import com.shadougao.email.entity.AddressBook;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddressBookDaoImpl extends BaseDaoImpl<AddressBook> implements AddressBookDao {
    public AddressBookDaoImpl(MongoTemplate mongoTemplate) {
        super(mongoTemplate, AddressBook.class, "t_address_book");
    }
}
