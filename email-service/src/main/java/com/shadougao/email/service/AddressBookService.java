package com.shadougao.email.service;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.AddressBook;

public interface AddressBookService extends IService<AddressBook> {

    /**
     * 添加联系人到通讯录
     * @param addressBook
     * @return
     */
    Result addContact(AddressBook addressBook);

    /**
     * 删除联系人
     * @param id
     * @return
     */
    Result delContact(String id);

    /**
     * 联系人添加到分组
     * @param id
     * @param groupId
     * @return
     */
    Result addToGroup(String id, String groupId);

    /**
     * 将联系人从分组中删除
     * @param id
     * @return
     */
    Result delToGroup(String id);
}
