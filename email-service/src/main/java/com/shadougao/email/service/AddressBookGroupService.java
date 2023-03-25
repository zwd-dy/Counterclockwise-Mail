package com.shadougao.email.service;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.AddressBookGroup;

public interface AddressBookGroupService extends IService<AddressBookGroup> {

    /**
     * 添加分组
     * @param group
     * @return
     */
    Result addGroup(AddressBookGroup group);

    /**
     * 删除分组
     * @param id
     * @return
     */
    Result delGroup(String id);

}
