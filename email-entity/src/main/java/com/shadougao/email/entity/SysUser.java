package com.shadougao.email.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统用户类
 */

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_user")
public class SysUser extends MySqlBaseEntity {

    /**
     * 自增id
     */
    @Id
    @Column(name = "user_id")
    @NotNull(groups = Update.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 登录名
     */
    private String login;

    /**
     * 登录密码
     */
    @JSONField(serialize = false)
    private String password;

    /**
     * 用户名
     */
    @NotBlank
    @Column(unique = true)
    private String username;

    /**
     * 邮箱
     */
    @Email
    private String email;

    /**
     * 是否启用 默认true
     */
    @NotNull
    private boolean enable;

}
