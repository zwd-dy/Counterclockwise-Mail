package com.shadougao.email.service.impl;

import cn.hutool.cron.pattern.CronPatternUtil;
import com.alibaba.fastjson.JSON;
import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.WsResult;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.CronUtil;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.dao.mongo.MailDao;
import com.shadougao.email.dao.mongo.SysEmailPlatformDao;
import com.shadougao.email.dao.mongo.UserBindEmailDao;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.entity.dto.PageData;
import com.shadougao.email.execute.MailParseExecute;
import com.shadougao.email.execute.SendMailExecute;
import com.shadougao.email.execute.MailExecutor;
import com.shadougao.email.service.MailService;
import com.shadougao.email.service.QuartzJobService;
import com.shadougao.email.service.UserBindEmailService;
import com.shadougao.email.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MailServiceImpl extends ServiceImpl<MailDao, Mail> implements MailService {

    private final UserBindEmailDao bindDao;
    private final SysEmailPlatformDao platformDao;
    private final MailExecutor executor;
    private final QuartzJobService jobService;

    @Override
    public void sendMail(Mail mail) {
        // 获取当前用户
        SysUser user = SecurityUtils.getCurrentUser();
        mail.setUserId(user.getId());
        // 获取发件邮箱
        UserBindEmail bindEmail = bindDao.getOneById(mail.getBindId());
        if (Objects.isNull(bindEmail)) {
            throw new BadRequestException("邮箱不存在");
        }
        // 获取发件邮箱的平台信息
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (Objects.isNull(platform)) {
            throw new BadRequestException("暂不支持该邮箱平台，请联系管理员");
        }

        //TODO
        Integer type = mail.getType();
        mail.setFrom(bindEmail.getEmailUser());
        mail.setSendState(MailEnum.SEND_ING);
        mail.setType(MailEnum.TYPE_SENT);
        mail.setSendExceptionLog("null");
        // 判断是否为草稿
        if (type != null && type == MailEnum.TYPE_DRAFT) {
            this.updateOne(mail);
        } else {
            mail = this.addOne(mail);
        }
        SendMailExecute execute = new SendMailExecute(bindEmail, platform, mail);
        executor.executeSend(execute);
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

    @Override
    public Result<?> saveDraft(Mail mail) {
        // 获取当前用户
        SysUser user = SecurityUtils.getCurrentUser();
        mail.setUserId(user.getId());
        // 获取发件邮箱
        UserBindEmail bindEmail = bindDao.getOneById(mail.getBindId());
        if (Objects.isNull(bindEmail)) {
            throw new BadRequestException("邮箱不存在");
        }
        // 获取发件邮箱的平台信息
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (Objects.isNull(platform)) {
            throw new BadRequestException("暂不支持该邮箱平台，请联系管理员");
        }

        Integer type = mail.getType();
        mail.setBindId(bindEmail.getId());
        mail.setFrom(bindEmail.getEmailUser());
        mail.setType(MailEnum.TYPE_DRAFT);
        // 判断是否为草稿
        if (type != null && type == MailEnum.TYPE_DRAFT) {
            this.updateOne(mail);
        } else {
            mail = this.addOne(mail);
        }
        return Result.success(this.addOne(mail));
    }

    @Override
    public Result<?> schedule(Mail mail) {
        // 获取当前用户
        SysUser user = SecurityUtils.getCurrentUser();
        mail.setUserId(user.getId());
        // 获取发件邮箱
        UserBindEmail bindEmail = bindDao.getOneById(mail.getBindId());
        if (Objects.isNull(bindEmail)) {
            throw new BadRequestException("邮箱不存在");
        }
        // 获取发件邮箱的平台信息
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (Objects.isNull(platform)) {
            throw new BadRequestException("暂不支持该邮箱平台，请联系管理员");
        }

        Integer type = mail.getType();
        mail.setType(MailEnum.TYPE_SCHEDULE);
        mail.setFrom(bindEmail.getEmailUser());
        mail.setSendState(MailEnum.SEND_ING);
        mail.setSendExceptionLog("null");
        // 判断是否为草稿
        if (type != null && type == MailEnum.TYPE_DRAFT) {
            mail.setType(MailEnum.TYPE_SCHEDULE);
            this.updateOne(mail);
        } else {
            mail = this.addOne(mail);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("mail", mail.getId());
//        秒 分 时 日 月 ? 月-月
        String cron = CronUtil.unixToCron(mail.getSendTime());
        jobService.addJob(mail.getId(), mail.getBindId()
                , cron, map);
        return Result.success(mail);
    }

    @Override
    public void delScheduleMail(List<Mail> mailList) {
        List<String> ids = new ArrayList<>();
        // 删除定时任务
        for (int i = 0; i < mailList.size(); i++) {
            Mail mail = mailList.get(i);
            ids.add(mail.getId());
            jobService.deleteJob(mail.getId(), mail.getBindId());
        }
        // 从数据库中删除
        this.batchDel(ids);
    }

    @Override
    public void updateSchedule(Mail mail) {
        // 获取当前用户
        SysUser user = SecurityUtils.getCurrentUser();
        mail.setUserId(user.getId());
        // 获取发件邮箱
        UserBindEmail bindEmail = bindDao.getOneById(mail.getBindId());
        if (Objects.isNull(bindEmail)) {
            throw new BadRequestException("邮箱不存在");
        }
        // 判断该任务是否已执行
        if (this.getOneById(mail.getId()).getType() != MailEnum.TYPE_SCHEDULE) {
            throw new BadRequestException("该邮件已被发送，任务已不存在");
        }
        // 重新设置cron表达式
        String cron = CronUtil.unixToCron(mail.getSendTime());
        jobService.updateJob(mail.getId(), mail.getBindId(), cron, null);
        // 更新到数据库
        this.updateOne(mail);
    }

    /**
     * 添加到标签
     *
     * @param mailList
     * @param tagIds
     */
    @Override
    public void addToTag(List<Mail> mailList, List<String> tagIds) {
        for (int i = 0; i < mailList.size(); i++) {
            Mail mail = mailList.get(i);
            String[] tagIdList = mail.getTagIds();
            Set<String> setTagIds;
            if (tagIdList == null) {
                setTagIds = new HashSet<>();
            } else {
                setTagIds = new HashSet<>(Arrays.asList(tagIdList));
            }
            // 加入标签id
            tagIds.forEach(item -> setTagIds.add(item));
            mail.setTagIds(setTagIds.toArray(new String[setTagIds.size()]));
            // 更新到数据库
            this.updateOne(mail);
        }
    }

    @Override
    public void delToTag(List<Mail> mailList) {
        for (int i = 0; i < mailList.size(); i++) {
            Mail mail = mailList.get(i);
            mail.setTagIds(new String[0]);
            this.updateOne(mail);
        }
    }

    @Override
    public void updateStar(List<Mail> mailList,Integer isStar) {
//        List<String> ids = new ArrayList<>();
//        mailList.forEach(item -> ids.add(item.getId()));
//        this.getBaseMapper().updateMulti(
//                new Query().addCriteria(Criteria.where("id").in(ids)),
//                new Update().set("isStar",isStar));

        mailList.forEach(item->{item.setIsStar(isStar);this.updateOne(item);});

    }


}
