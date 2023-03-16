package com.shadougao.email.config.security.auth;

import com.shadougao.email.common.utils.TokenProvider;
import com.shadougao.email.config.security.bean.OnlineUserService;
import com.shadougao.email.config.security.bean.SecurityProperties;
import com.shadougao.email.config.security.bean.UserCacheClean;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class TokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final SecurityProperties securityProperties;
    private final OnlineUserService onlineUserService;
    private final TokenProvider tokenProvider;
    private final UserCacheClean userCacheClean;

    @Override
    public void configure(HttpSecurity http) {
        TokenFilter customFilter = new TokenFilter(securityProperties, onlineUserService, tokenProvider, userCacheClean);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
