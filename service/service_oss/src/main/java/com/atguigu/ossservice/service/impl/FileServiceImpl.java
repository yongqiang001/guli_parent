package com.atguigu.ossservice.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.atguigu.ossservice.utils.ConstantPropertiesUtil;
import com.atguigu.ossservice.service.FileService;
import com.atguigu.servicebase.handler.GuliException;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    //上传文件到阿里云OSS
    @Override
    public String uploadFileOss(MultipartFile file) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = ConstantPropertiesUtil.END_POINT;
// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;


        // 上传文件流。
        try {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            InputStream inputStream = file.getInputStream();
            String fileName = file.getOriginalFilename();
            //UUID
            String uuid = UUID.randomUUID().toString();
            //拼接
            fileName = uuid+fileName;

            //定义文件目录
            //目录/2020/06/22/0.JPG
            String path = new DateTime().toString("yyyy/MM/dd");
            fileName = path + "/" + fileName;


            ossClient.putObject(bucketName, fileName, inputStream);

            // 关闭OSSClient。
            ossClient.shutdown();

            //拼接url
            String url = "https://" + bucketName + "." + endpoint + "/" + fileName;
            return url;

        } catch (IOException e) {
            throw new GuliException(20001, "上传失败");
        }
    }
}
