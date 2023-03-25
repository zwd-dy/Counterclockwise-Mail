package com.shadougao.email.service.impl;

import com.shadougao.email.dao.mongo.MailFileDao;
import com.shadougao.email.entity.MailFile;
import com.shadougao.email.service.MailFileService;
import org.springframework.stereotype.Service;

@Service
public class MailFileServiceImpl extends ServiceImpl<MailFileDao, MailFile> implements MailFileService {
}
