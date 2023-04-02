package com.shadougao.email.execute;

import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.service.MailService;
import com.shadougao.email.service.SysEmailPlatformService;
import com.shadougao.email.service.UserBindEmailService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class QuartzSendMailJob implements Job {

    @Autowired
    private MailService mailService;
    @Autowired
    private UserBindEmailService bindEmailService;
    @Autowired
    private SysEmailPlatformService platformService;
    @Autowired
    private MailExecutor executor;


    @Override
    public void execute(JobExecutionContext context) {
        try {
            log.info(context.getScheduler().getSchedulerInstanceId() + "--" + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
            String mailId = (String) context.getTrigger().getJobDataMap().get("mail");
            // 获取邮箱实体
            Mail mail = mailService.getOneById(mailId);
            if (mail != null) {
                // 获取对应发送邮箱实体
                UserBindEmail bindEmail = bindEmailService.getOneById(mail.getBindId());
                // 获取邮箱平台实体
                SysEmailPlatform platform = platformService.getOneById(bindEmail.getPlatformId());
                // 执行发送
                mail.setSendState(MailEnum.SEND_ING);
                mail.setType(MailEnum.TYPE_SENT);
                mail.setSendExceptionLog("null");
                SendMailExecute execute = new SendMailExecute(bindEmail, platform, mail);
                executor.executeSend(execute);
            }


        } catch (SchedulerException e) {
            log.error("任务执行失败", e);
        }
    }
}
