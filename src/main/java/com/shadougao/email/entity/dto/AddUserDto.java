package com.shadougao.email.entity.dto;

import com.shadougao.email.entity.SysUser;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddUserDto {

    @NotNull
    private SysUser sysUser;

    @NotBlank(message = "密码不能为空!")
    private String password;

    @NotBlank(message = "请确认密码!")
    private String confirmPassword;

}
