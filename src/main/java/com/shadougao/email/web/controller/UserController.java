package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.dto.AddUserDto;
import com.shadougao.email.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<?> registerUser(@RequestBody @Validated AddUserDto resource) {
        userService.addUser(resource);
        return Result.success();
    }

}
