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
import com.shadougao.email.execute.SendMailExecute;
import com.shadougao.email.execute.SendMailExecutor;
import com.shadougao.email.service.MailService;
import com.shadougao.email.utils.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
        mail.setUserId(user.getId());
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
        SendMailExecute execute = new SendMailExecute(bindEmail, platform, mail);
        executor.execute(execute);
    }

    @Override
    public void delMail(List<Mail> mailList) {
        List<String> ids = new ArrayList<>();
        Map<String, List<Mail>> map = new HashMap<>();
        // group by bindId
        for (int i = 0; i < mailList.size(); i++) {
            Mail mail = mailList.get(i);
            List<Mail> mails = map.get(mail.getBindId());
            if (mails == null) mails = new ArrayList<>();
            mails.add(mail);
            map.put(mail.getBindId(), mails);
            ids.add(mail.getId());
        }
        // 调用javamail删除
        Set<String> bindIds = map.keySet();
        for (String bindId : bindIds) {
            UserBindEmail bindEmail = bindDao.getOneById(bindId);
            List<Long> uids = new ArrayList<>();
            List<Mail> mail = map.get(bindId);
            SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
            for (int i = 0; i < mail.size(); i++) {
                String uid = mail.get(i).getUid();
                if (uid == null) {
                    continue;
                }
                uids.add(Long.parseLong(uid));
            }
//            mail.forEach(item -> uids.add(Long.parseLong(item.getUid())));
            EmailUtil.delMail(platform, bindEmail, uids);
        }
        // 从数据库中删除
        this.batchDel(ids);
    }

}
