package com.shadougao.email.dao;

import com.shadougao.email.entity.UserBindEmail;

import java.util.List;

public interface UserBindEmailDao extends BaseDao<UserBindEmail> {

    UserBindEmail getByEmailUser(Integer userId,String emailUser);

    List<UserBindEmail> emailBindList(Integer id);

}
