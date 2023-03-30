package com.shadougao.email.service;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.MailTag;

public interface MailTagService extends IService<MailTag> {

    Result<?> addTag(MailTag mailTag);
    Result<?> delTag(String tagId);
    Result<?> getTag();

}
