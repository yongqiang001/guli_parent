package com.atguigu.ossservice.controller;

import com.atguigu.ossservice.service.FileService;
import com.atguigu.commonutils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(description = "上传文件管理")
@RestController
@RequestMapping("/eduoss/fileoss")
@CrossOrigin
public class FileOssController {

    @Autowired
    private FileService fileService;

    @ApiOperation(value = "上传文件")
    @PostMapping("fileUpload")
    public R fileUploadOss(MultipartFile file){
        // 1.得到上传文件

        // 2.上传到阿里云OSS
        String url = fileService.uploadFileOss(file);
        // 3.返回OSS地址
        return R.ok().data("url", url);


    }



}
