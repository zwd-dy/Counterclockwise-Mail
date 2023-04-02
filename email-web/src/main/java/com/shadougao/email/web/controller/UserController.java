package com.shadougao.email.web.controller;

import com.shadougao.email.common.result.Result;
import com.shadougao.email.entity.AddUserDto;
import com.shadougao.email.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getValidCode")
    public Result<?> getValidCode(@RequestParam String username, @RequestParam String email) {
        userService.getValidCode(username, email);
        return Result.success();
    }

}
