package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.SysUser;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.service.UserBindEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Result emailBind(@RequestBody UserBindEmail bindEmail) {
        return bindService.emailBind(bindEmail);
    }

    @PostMapping("/email/remove/{id}")
    public Result emailRemove(@PathVariable("id") String id){
        return bindService.emailRemove(id);
    }

    @PostMapping("/email/update")
    public Result emailUpdate(@RequestBody UserBindEmail bindEmail){
        return bindService.emailUpdate(bindEmail);
    }

}
