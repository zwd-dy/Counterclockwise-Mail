package com.shadougao.email.dao.mongo;

import com.shadougao.email.entity.AddressBook;

public interface AddressBookDao extends BaseDao<AddressBook> {
    AddressBook getOneByEmailUser(Long userId,String email);

}
