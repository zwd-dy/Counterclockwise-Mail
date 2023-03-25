package com.shadougao.email.service.impl;

import cn.hutool.core.util.StrUtil;
import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.common.utils.AssertUtil;
import com.shadougao.email.common.utils.RsaUtils;
import com.shadougao.email.config.RsaProperties;
import com.shadougao.email.dao.mysql.SysUserRepository;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.dto.AddUserDto;
import com.shadougao.email.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void addUser(AddUserDto resource) {
        SysUser user = resource.getSysUser();
        SysUser sysUser = sysUserRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail());
        if (Objects.nonNull(sysUser)) {
            AssertUtil.validParam(Objects.equals(sysUser.getUsername(), user.getUsername()), "用户名已存在!");
            AssertUtil.validParam(Objects.equals(sysUser.getEmail(), user.getEmail()), "邮箱已存在!");
        }
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
        sysUserRepository.save(user);
    }
}
