package com.shadougao.email.service.impl;

import com.shadougao.email.dao.AddressBookDao;
import com.shadougao.email.entity.AddressBook;
import com.shadougao.email.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {

}
