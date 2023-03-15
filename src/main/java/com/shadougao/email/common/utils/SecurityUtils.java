package com.shadougao.email.common.utils;

import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.dto.JwtUserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static SysUser getCurrentUser() {
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        JwtUserDto loginUser = (JwtUserDto) authentication.getPrincipal();
        SysUser user = loginUser.getUser();
        if (user == null) {
            throw new BadRequestException("请先登录！");
        }
        return user;
    }

}
