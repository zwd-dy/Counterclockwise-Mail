package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * 用户绑定邮箱平台信息
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_user_bind_email")
public class UserBindEmail extends MongoBaseEntity {
    /**
     * 平台id [SysEmailPlatform]
     */
    private String platformId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 登录邮箱账号
     */
    private String emailUser;
    /**
     * 登录邮箱密码
     */
    private String emailAuth;
    /**
     * 0/未在同步 1/正在同步
     */
    private Integer synchronizing;

    @Override
    public String toString() {
        return "UserBindEmail{" +
                "platformId='" + platformId + '\'' +
                ", userId='" + userId + '\'' +
                ", emailUser='" + emailUser + '\'' +
                ", emailAuth='" + emailAuth + '\'' +
                '}';
    }
}
