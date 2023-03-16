package com.shadougao.email.dao;

import com.shadougao.email.entity.UserBindEmail;

public interface UserBindEmailDao extends BaseDao<UserBindEmail> {

    UserBindEmail getByEmailUser(Integer userId,String emailUser);
}
