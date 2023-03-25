package com.shadougao.email.dao;

import com.shadougao.email.entity.AddressBook;

public interface AddressBookDao extends BaseDao<AddressBook> {

    AddressBook getOneByEmailUser(String userId,String email);

}
