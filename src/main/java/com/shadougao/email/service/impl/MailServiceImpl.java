package com.shadougao.email.service.impl;

import com.shadougao.email.dao.MailDao;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.service.MailService;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl extends ServiceImpl<MailDao, Mail> implements MailService {
}
