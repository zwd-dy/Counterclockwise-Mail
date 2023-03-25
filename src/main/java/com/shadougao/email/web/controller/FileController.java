package com.shadougao.email.web.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.shadougao.email.common.result.Result;
import com.shadougao.email.common.utils.SecurityUtils;
import com.shadougao.email.entity.MailFile;
import com.shadougao.email.service.MailFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${upload.upload-path}")
    private String uploadPath;
    @Autowired
    private MailFileService fileService;


    public static void main(String[] args) {
        File file = new File("J:\\file\\static\\2\\Snipaste_2023-03-21_18-40-14.jpg");
        System.out.println(file.getParent());
    }

    @PostMapping("/upload")
    @ResponseBody
    public Result<?> handleFileUpload(MultipartFile file) {
        Long userId = SecurityUtils.getCurrentUser().getId();
        String filename = file.getOriginalFilename();
        String path =  "/" + userId + "/" + filename;
        File saveFile = new File(uploadPath + path);
        // 创建用户目录
//        FileUtil.mkdir(saveFile.getParent());
        if(!saveFile.getParentFile().exists()){
            saveFile.getParentFile().mkdir();
        }

        MailFile uploadFile = null;


        try {
            // 保存到本地
            file.transferTo(saveFile);

            // 记录到数据库
            MailFile mailFile = new MailFile();
            mailFile.setName(filename);
            mailFile.setSize(file.getSize());
            mailFile.setAbsolutePath(saveFile.getAbsolutePath());
            mailFile.setRelativePath(path);
            mailFile.setFormat(FileUtil.getSuffix(filename));
            //TODO 获取md5后会一直占用文件（未解决）
//            mailFile.setMd5(DigestUtils.md5Hex(new FileInputStream(mailFile.getAbsolutePath())));
            mailFile.setUploader(userId);
            mailFile.setUploadTime(DateUtil.formatDateTime(new Date()));
            uploadFile = fileService.addOne(mailFile);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Result.success(uploadFile);
    }


}
