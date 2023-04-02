package com.shadougao.email.dao.mongo;

import com.shadougao.email.entity.Mail;

import java.util.List;

public interface MailDao extends BaseDao<Mail> {
    /**
     * 根据邮箱UID和邮箱账号id查找邮箱
     * @param uid
     * @param bindId
     * @return
     */
    List<Mail> findByUidAndBindId(List<String> uid,String bindId);
}
