package com.shadougao.email.service.impl;

import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.dao.mongo.MailDao;
import com.shadougao.email.dao.mongo.SysEmailPlatformDao;
import com.shadougao.email.dao.mongo.UserBindEmailDao;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.execute.SendMailExecutor;
import com.shadougao.email.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MailServiceImpl extends ServiceImpl<MailDao, Mail> implements MailService {

    private final UserBindEmailDao bindDao;
    private final SysEmailPlatformDao platformDao;
    private final SendMailExecutor executor;

    @Override
    public void sendMail(Mail mail) {
        // 获取当前用户
        SysUser user = SecurityUtils.getCurrentUser();
        // 获取发件邮箱
        UserBindEmail bindEmail = bindDao.getOneById(mail.getFrom());
        if (Objects.isNull(bindEmail)) {
            throw new BadRequestException("邮箱不存在");
        }
        // 获取发件邮箱的平台信息
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (Objects.isNull(platform)) {
            throw new BadRequestException("暂不支持该邮箱平台，请联系管理员");
        }

        //TODO
        mail.setFrom(bindEmail.getEmailUser());
        mail.setSendState(MailEnum.SEND_ING);
        mail.setType(MailEnum.TYPE_SENT);
        mail.setSendExceptionLog("null");
        mail = this.addOne(mail);
        SendMailExecute execute = new SendMailExecute(bindEmail, platform, mail, false);
        executor.execute(execute);
    }
}
