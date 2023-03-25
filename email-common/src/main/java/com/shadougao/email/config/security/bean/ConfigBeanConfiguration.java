package com.shadougao.email.config.security.bean;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigBeanConfiguration {

//    登录配置
//    @Bean
//    @ConfigurationProperties(prefix = "login")
//    public LoginProperties loginProperties() {
//        return new LoginProperties();
//    }

    @Bean
    @ConfigurationProperties(prefix = "jwt")
    public SecurityProperties securityProperties() {
        return new SecurityProperties();
    }
}