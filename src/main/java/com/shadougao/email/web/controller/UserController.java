package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.service.UserBindEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserBindEmailService bindService;

    /**
     * 邮箱平台绑定
     * @param user         用户信息
     * @param bindEmail    绑定信息
     * @return
     */
    @PostMapping("/email/bind")
    public Result emailBind(@RequestBody SysUser user, @RequestBody UserBindEmail bindEmail) {
        return bindService.emailBind(user,bindEmail);
    }

}
