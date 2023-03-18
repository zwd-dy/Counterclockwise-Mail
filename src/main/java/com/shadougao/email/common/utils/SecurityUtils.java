package com.shadougao.email.common.utils;

import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.config.security.bean.SpringContextHolder;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.dto.JwtUserDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class SecurityUtils {

//    public static UserDetails getCurrentUser() {
//        UserDetailsService userDetailsService = SpringContextHolder.getBean(UserDetailsService.class);
//        return userDetailsService.loadUserByUsername(getCurrentUsername());
//    }

    public static SysUser getCurrentUser() {
        UserDetailsService userDetailsService = SpringContextHolder.getBean(UserDetailsService.class);
        JwtUserDto jwtUserDto = (JwtUserDto) userDetailsService.loadUserByUsername(getCurrentUsername());
        return jwtUserDto.getUser();
    }
    /**
     * 获取系统用户名称
     *
     * @return 系统用户名称
     */
    public static String getCurrentUsername() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期");
        }
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
    }


}
