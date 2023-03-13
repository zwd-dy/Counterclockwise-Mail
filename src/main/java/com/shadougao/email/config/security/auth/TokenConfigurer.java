package com.shadougao.email.config.security.auth;

import com.shadougao.email.config.security.bean.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class TokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final SecurityProperties securityProperties;

    @Override
    public void configure(HttpSecurity http) {
        TokenFilter customFilter = new TokenFilter(securityProperties);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
