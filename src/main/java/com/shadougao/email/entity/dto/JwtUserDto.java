package com.shadougao.email.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.shadougao.email.entity.SysUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class JwtUserDto implements UserDetails {

    private final SysUser user;

    @JSONField(serialize = false)
    private final List<GrantedAuthority> authorities;

    public JwtUserDto(SysUser user, List<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    public Set<String> getRoles() {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JSONField(serialize = false)
    public String getUsername() {
        return user.getUsername();
    }

    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isEnabled() {
        return user.isEnable();
    }
}
