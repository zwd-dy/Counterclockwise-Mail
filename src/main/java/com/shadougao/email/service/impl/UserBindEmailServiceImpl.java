package com.shadougao.email.service.impl;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.SendMailUtil;
import com.shadougao.email.dao.SysEmailPlatformDao;
import com.shadougao.email.dao.UserBindEmailDao;
import com.shadougao.email.entity.SysEmailPlatform;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.entity.dto.JwtUserDto;
import com.shadougao.email.service.UserBindEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserBindEmailServiceImpl extends ServiceImpl<UserBindEmailDao, UserBindEmail> implements UserBindEmailService {

    private final UserBindEmailDao bindEmailDao;
    private final SysEmailPlatformDao platformDao;

    @Override
    public Result emailBind(SysUser user, UserBindEmail bindEmail) {

        // 判断邮箱平台是否存在
        SysEmailPlatform platform = platformDao.getOneById(bindEmail.getPlatformId());
        if (Objects.isNull(platform)) {
            throw new BadRequestException("暂不支持该邮箱平台！");
        }
        // 验证邮箱信息
        if (!SendMailUtil.check(platform,bindEmail)) {
            throw new BadRequestException("邮箱账号验证失败，请仔细检查账号和密码！");
        }

        // 完成绑定
        if (Objects.isNull(bindEmailDao.addOne(bindEmail))) {
            throw new BadRequestException("绑定失败，请重试");
        }
        return Result.success(bindEmail);
    }

}
