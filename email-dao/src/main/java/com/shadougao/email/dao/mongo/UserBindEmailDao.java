package com.shadougao.email.dao.mongo;

import com.shadougao.email.entity.UserBindEmail;

import java.util.List;

public interface UserBindEmailDao extends BaseDao<UserBindEmail> {

    UserBindEmail getByEmailUser(Long userId,String emailUser);

    List<UserBindEmail> emailBindList(Long userId);

}
