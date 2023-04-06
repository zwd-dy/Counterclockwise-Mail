package com.shadougao.email.service.impl;

import com.alibaba.fastjson.JSON;
import com.shadougao.email.common.result.MailEnum;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.WsResult;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.GetBeanUtil;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.dao.mongo.SysEmailPlatformDao;
import com.shadougao.email.dao.mongo.UserBindEmailDao;
import com.shadougao.email.entity.Mail;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.execute.MailExecutor;
import com.shadougao.email.execute.MailParseExecute;
import com.shadougao.email.execute.SendMailExecute;
import com.shadougao.email.listener.RedisMainListener;
import com.shadougao.email.service.MailService;
import com.shadougao.email.service.UserBindEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserBindEmailServiceImpl extends ServiceImpl<UserBindEmailDao, UserBindEmail> implements UserBindEmailService {

    private final UserBindEmailDao bindEmailDao;
    private final SysEmailPlatformDao platformDao;
    private final MailService mailService;
    private final MailExecutor mailExecutor;
    private final RedisUtil redisUtil;

    private static final String PREFIX = "email:lockbind";

    /**
     * 用户绑定邮箱
     *
     * @param bindEmail
     * @return
     */
    @Override
    public Result emailBind(UserBindEmail bindEmail) {
        SysUser user = SecurityUtils.getCurrentUser();

        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        // 判断邮箱平台是否存在
        if (Objects.isNull(platform)) {
            throw new BadRequestException("暂不支持该邮箱平台！");
        }
        // 判断账号是否已绑定
        if (!Objects.isNull(bindEmailDao.getByEmailUser(user.getId(), bindEmail.getEmailUser()))) {
            throw new BadRequestException("该邮箱账号已绑定");
        }
        // 验证邮箱信息
        if (!SendMailExecute.checkAuth(platform, bindEmail)) {
            throw new BadRequestException("邮箱账号验证失败，请仔细检查账号和密码！");
        }

        // 完成绑定
        bindEmail.setUserId(user.getId());
        bindEmail.setSynchronizing(0);
        UserBindEmail newBind = bindEmailDao.addOne(bindEmail);
        if (Objects.isNull(newBind)) {
            throw new BadRequestException("绑定失败，请重试");
        }

        // 开启同步邮箱线程
//        MailParseExecute parse = new MailParseExecute();
//        parse.setBindEmail(bindEmail);
//        parse.setPlatform(platform);
//        parse.setBatch(true);
//        mailExecutor.getParseExecutorService().execute(parse);
        // 为某节点添加分配任务
        new RedisMainListener().addTask(newBind);
        return Result.success(newBind);
    }

    @Override
    public Result emailRemove(String id) {
        // 判断账号是否已绑定
        UserBindEmail bindEmail = bindEmailDao.getOneById(id);
        if (Objects.isNull(bindEmail)) {
            throw new BadRequestException("你已解绑该邮箱");
        }

        // 完成解绑
        mailService.batchDel(new Query().addCriteria(Criteria.where("bindId").is(id)));
        bindEmailDao.delOne(id);
        // 为某节点删除该任务
        new RedisMainListener().delTask(id);
//        GetBeanUtil.getApplicationContext().getBean(RedisMainListener.class).delTask(id);
        return Result.success("解绑成功");
    }

    @Override
    public Result emailUpdate(UserBindEmail bindEmail) {
        // 判断账号是否已绑定
        if (Objects.isNull(bindEmailDao.getOneById(bindEmail.getId()))) {
            throw new BadRequestException("修改失败，你似乎没有绑定该邮箱");
        }
        // 验证邮箱信息
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (!SendMailExecute.checkAuth(platform, bindEmail)) {
            throw new BadRequestException("邮箱账号验证失败，请仔细检查账号和密码！");
        }

        // 完成修改
        if (bindEmailDao.updateOne(bindEmail) != 1) {
            throw new BadRequestException("修改失败，请重新操作");
        }
        return Result.success("修改成功");
    }

    @Override
    public Result emailBindList() {
        SysUser user = SecurityUtils.getCurrentUser();
        List<UserBindEmail> bindEmails = bindEmailDao.emailBindList(user.getId());
        return Result.success(bindEmails);
    }

    @Override
    public void pullMail(String bindId) {
        UserBindEmail bindEmail = bindEmailDao.getOneById(bindId);
        if (Objects.isNull(bindEmail)) {
            throw new BadRequestException("邮箱账号不存在");
        }
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (Objects.isNull(platform)) {
            throw new BadRequestException("邮箱平台不存在，请联系管理员");
        }
        // 查看是否有其他账号在同步中
        if (this.isPull()) {
            throw new BadRequestException("请等待其他邮箱同步完成！");
        }

        // 通知前端邮件准备
        WebSocket.sendOneMessage(String.valueOf(bindEmail.getUserId()), JSON.toJSONString(WsResult.message(WsResult.PULL_READY, null)));
        MailParseExecute parse = new MailParseExecute();
        parse.setBindEmail(bindEmail);
        parse.setPlatform(platform);
        parse.setBatch(true);
        mailExecutor.getParseExecutorService().execute(parse);
    }

    @Override
    public boolean isPull() {
        SysUser user = SecurityUtils.getCurrentUser();
        List<UserBindEmail> bindEmails = bindEmailDao.emailBindList(user.getId());
        for (int i = 0; i < bindEmails.size(); i++) {
            if ("1".equals(String.valueOf(redisUtil.hget(PREFIX, bindEmails.get(i).getId())))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void lockBindMail(String bindId) {
        redisUtil.hset(PREFIX, bindId, "1", 300);
    }

    @Override
    public void unlockBindMail(String bindId) {
        redisUtil.hdel(PREFIX, bindId);
    }
}
