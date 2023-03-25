package com.shadougao.email.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_file")
public class MailFile extends BaseEntity {
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件大小
     */
    private long size;
    /**
     * 文件格式
     */
    private String format;
    /**
     * 上传时间
     */
    private String uploadTime;
    /**
     * 上传者
     */
    private String uploader;
    /**
     * 绝对路径
     */
    private String absolutePath;
    /**
     * 相对路径
     */
    private String relativePath;
    /**
     * md5校验
     */
    private String md5;
}
