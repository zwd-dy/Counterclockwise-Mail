package com.shadougao.email.web.controller;

import com.shadougao.email.EmailApplication;
import com.shadougao.email.common.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    @Value("${upload.upload-path}")
    private String uploadPath;

    @PostMapping( "/upload")
    @ResponseBody
    public Result<?> handleFileUpload(MultipartFile file)  {
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        try {
            file.transferTo(new File(uploadPath+filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Result.success("测试");
    }


}
