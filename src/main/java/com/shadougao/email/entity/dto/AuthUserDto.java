package com.shadougao.email.entity.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class AuthUserDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String code;

    private String uuid = "";
}
