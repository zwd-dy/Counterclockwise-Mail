package com.shadougao.email.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.AssertUtil;
import com.shadougao.email.common.utils.CacheKey;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.common.utils.RsaUtils;
import com.shadougao.email.config.RsaProperties;
import com.shadougao.email.dao.mysql.SysUserRepository;
import com.shadougao.email.entity.AddUserDto;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.service.UserService;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtil redisUtil;

    @Override
    @Transactional(rollbackFor = Exception.class,transactionManager = "transactionManager")
    public void addUser(AddUserDto resource) {
        /* 判断验证码是否存在 */
        String key = CacheKey.USER_ADD + resource.getSysUser().getEmail();
        AssertUtil.validParam(!redisUtil.hasKey(key), "验证码不存在或已过期!");
        AssertUtil.validParam(!Objects.equals(redisUtil.get(key), resource.getCode()), "验证码错误!");
        SysUser user = resource.getSysUser();
        /* 用户名与邮箱均为唯一字段 */
        SysUser sysUser = sysUserRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (Objects.nonNull(sysUser)) {
            AssertUtil.validParam(Objects.equals(sysUser.getUsername(), user.getUsername()), "用户名已存在!");
            AssertUtil.validParam(Objects.equals(sysUser.getEmail(), user.getEmail()), "邮箱已存在!");
        }
        /* 密码 确认密码 */
        String pass, confirmPass;
        try {
            pass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, resource.getPassword());
            confirmPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, resource.getConfirmPassword());
        } catch (Exception e) {
            throw new BadRequestException("解密失败!");
        }
        AssertUtil.validParam(StrUtil.isBlank(pass) || StrUtil.isBlank(confirmPass), "请输入密码!");
        AssertUtil.validParam(!Objects.equals(pass, confirmPass), "两次密码输入不一致!");
        /* 密码加密 */
        resource.getSysUser().setPassword(passwordEncoder.encode(pass));
        user.setEnable(true);
        sysUserRepository.save(user);
        /* 删除缓存 */
        redisUtil.del(key);
    }

    @Override
    public void getValidCode(String username, String email) {
        if(StringUtil.isNullOrEmpty(email)){
            throw new BadRequestException("请输入邮箱");
        }
        /* 用户名与邮箱均为唯一字段 */
        SysUser sysUser = sysUserRepository.findByUsernameOrEmail(username, email);
        if (Objects.nonNull(sysUser)) {
            AssertUtil.validParam(Objects.equals(sysUser.getUsername(), username), "用户名已存在!");
            AssertUtil.validParam(Objects.equals(sysUser.getEmail(), email), "邮箱已存在!");
        }
        sendValidCode(email);

    }

    @Async
    public void sendValidCode(String email) {
        String code = RandomUtil.randomNumbers(6);
        redisUtil.set(CacheKey.USER_ADD + email, code, 3, TimeUnit.MINUTES);
        MailUtil.send(email, "【多邮件收发系统】", String.format("注册验证码为: %s 有效期为3分钟", code), true);
    }
}
