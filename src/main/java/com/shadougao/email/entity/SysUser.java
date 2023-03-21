package com.shadougao.email.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 系统用户类
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class SysUser extends BaseEntity{

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
    private String username;

    /**
     * 是否启用 默认true
     */
    private boolean enable;

}
