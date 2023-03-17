package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.EncryptUtils;
import com.shadougao.email.config.security.bean.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/online")
public class OnlineController {

    private final OnlineUserService onlineUserService;

    @GetMapping
    public Result<?> query(String filter) {
        return Result.success(onlineUserService.getAll(filter));
    }

    @DeleteMapping
    public Result<?> delete(@RequestBody Set<String> keys) throws Exception {
        for (String key : keys) {
            // 解密Key
            key = EncryptUtils.desDecrypt(key);
            onlineUserService.kickOut(key);
        }
        return Result.success();
    }
}
