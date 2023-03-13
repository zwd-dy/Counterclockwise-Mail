package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 邮箱平台类
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("sys_email_platform")
public class SysEmailPlatform extends BaseEntity{
    /**
     * 平台名字（QQ邮箱、126邮箱、gmail邮箱）
     */
    private String name;
}
