package com.shadougao.email.dao;

import com.shadougao.email.entity.AddressBook;

public interface AddressBookDao extends BaseDao<AddressBook> {

    AddressBook getOneByEmailUser(Integer userId,String email);

}
