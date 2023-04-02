package com.shadougao.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * 邮箱平台类
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("sys_email_platform")
public class SysEmailPlatform extends MongoBaseEntity {
    /**
     * 平台名字（QQ邮箱、126邮箱、gmail邮箱）
     */
    private String name;
    private Map<String, Connect> connect;


    @Setter
    @Getter
    public static class Connect {
        /**
         * 邮箱服务器地址
         */
        private String host;
        /**
         * 端口
         */
        private String port;
        /**
         * 附加连接参数
         */
        List<Map<String, String>> props;
        /**
         * 客户端参数
         */
        List<Map<String, String>> clientParams;
    }


    @Override
    public String toString() {
        return "SysEmailPlatform{" +
                "name='" + name + '\'' +
                ", connect=" + connect +
                '}';
    }
}

