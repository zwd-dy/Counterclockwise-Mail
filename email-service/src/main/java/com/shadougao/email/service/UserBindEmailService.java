package com.shadougao.email.service;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.UserBindEmail;

public interface UserBindEmailService extends IService<UserBindEmail> {

    /**
     * 用户绑定邮箱
     * @param bindEmail
     * @return
     */
    Result emailBind(UserBindEmail bindEmail);

    Result emailRemove(String id);

    Result emailUpdate(UserBindEmail bindEmail);

    Result emailBindList();

}
