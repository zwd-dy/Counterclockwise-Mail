package com.shadougao.email.config.security.bean;

import com.shadougao.email.common.result.exception.BadRequestException;
import com.shadougao.email.dao.mysql.SysUserRepository;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.dto.JwtUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserRepository sysUserRepository;

    /**
     * 用户信息缓存
     *
     * @see {@link UserCacheClean}
     */
    static Map<String, JwtUserDto> userDtoCache = new ConcurrentHashMap<>();

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        JwtUserDto jwtUserDto;
        SysUser user;
        try {
            user = sysUserRepository.findByUsername(username);
        } catch (BadRequestException e) {
            // SpringSecurity会自动转换UsernameNotFoundException为BadCredentialsException
            throw new BadRequestException(e.getMessage());
        }
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        } else {
            if (!user.isEnable()) {
                throw new BadRequestException("账号未激活！");
            }
            jwtUserDto = new JwtUserDto(user, new ArrayList<>());
            userDtoCache.put(username, jwtUserDto);
        }
        return jwtUserDto;
    }
}
