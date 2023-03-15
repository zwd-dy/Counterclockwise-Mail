package com.shadougao.email.service;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;

public interface UserBindEmailService extends IService<UserBindEmail> {
    Result emailBind(SysUser user, UserBindEmail bindEmail);
}
