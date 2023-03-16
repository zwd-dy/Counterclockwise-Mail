package com.shadougao.email.web.controller;


import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.JwtUtil;
import com.shadougao.email.common.utils.RedisUtil;
import com.shadougao.email.config.security.bean.OnlineUserService;
import com.shadougao.email.config.security.bean.SecurityProperties;
import com.shadougao.email.entity.dto.AuthUserDto;
import com.shadougao.email.entity.dto.JwtUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthorizationController {

    private final SecurityProperties properties;
    private final RedisUtil redisUtil;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final OnlineUserService onlineUserService;

    @PostMapping
    public Result<?> login(@Validated @RequestBody AuthUserDto authUser, HttpServletRequest request) throws Exception {
        // 密码解密
//        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());

        // 查询验证码
//        String code = (String) redisUtil.get(authUser.getUuid());
        // 清除验证码
//        redisUtil.del(authUser.getUuid());
//        if (StrUtil.isBlank(code)) {
//            throw new BadRequestException("验证码不存在或已过期");
//        }
//        if (StrUtil.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
//            throw new BadRequestException("验证码错误");
//        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), authUser.getPassword());
        Authentication authentication;
        authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = JwtUtil.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);
        // 返回 token 与 用户信息
        Map<String, Object> authInfo = new HashMap<>(2) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
        }};
        return Result.success(authInfo);
    }
}
