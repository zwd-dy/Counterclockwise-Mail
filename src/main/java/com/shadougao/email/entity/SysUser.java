package com.shadougao.email.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 系统用户类
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class SysUser {
    @TableId
    private Integer id;
    /**
     * 登录名
     */
    private String login;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;
    /**
     * 是否启用 默认true
     */
    private boolean enable;
}
