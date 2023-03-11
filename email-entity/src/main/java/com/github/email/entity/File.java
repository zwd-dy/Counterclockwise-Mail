package com.github.email.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("t_file")
public class File {

    @Id
    private String id;
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
