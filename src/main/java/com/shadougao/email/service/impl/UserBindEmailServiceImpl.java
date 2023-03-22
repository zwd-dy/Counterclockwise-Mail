package com.shadougao.email.service.impl;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.common.utils.SendMailUtil;
import com.shadougao.email.dao.SysEmailPlatformDao;
import com.shadougao.email.dao.UserBindEmailDao;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.entity.dto.JwtUserDto;
import com.shadougao.email.execute.SendMailExecute;
import com.shadougao.email.service.UserBindEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserBindEmailServiceImpl extends ServiceImpl<UserBindEmailDao, UserBindEmail> implements UserBindEmailService {

    private final UserBindEmailDao bindEmailDao;
    private final SysEmailPlatformDao platformDao;

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
        UserBindEmail newBind = bindEmailDao.addOne(bindEmail);
        if (Objects.isNull(newBind)) {
            throw new BadRequestException("绑定失败，请重试");
        }
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
        bindEmailDao.delOne(id);
        //TODO 解绑后的其他操作
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

}
