package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.UserBindEmail;
import com.shadougao.email.service.UserBindEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/platform")
@RequiredArgsConstructor
public class SysEmailPlatformController {

    private final UserBindEmailService bindService;

    @GetMapping("/bind/list")
    public Result<?> emailBindList(){
        return bindService.emailBindList();
    }
    /**
     * 邮箱平台绑定
     * @param bindEmail    绑定信息
     * @return
     */
    @PostMapping("/bind")
    public Result<?> emailBind(@RequestBody UserBindEmail bindEmail) {
        return bindService.emailBind(bindEmail);
    }

    /**
     * 邮箱平台解除绑定
     * @param id
     * @return
     */
    @DeleteMapping("/remove/{id}")
    public Result<?> emailRemove(@PathVariable("id") String id){
        return bindService.emailRemove(id);
    }

    /**
     * 邮箱平台信息更改
     * @param bindEmail
     * @return
     */
    @PostMapping("/update")
    public Result<?> emailUpdate(@RequestBody UserBindEmail bindEmail){
        return bindService.emailUpdate(bindEmail);
    }

}
