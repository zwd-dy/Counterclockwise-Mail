package com.github.email.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("t_file")
public class File extends BaseEntity {
    /**
     * 文件名
     */
    private String name;
    /**
     * 文件大小
     */
    private String size;
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
     * md5校验
     */
    private String md5;
}
