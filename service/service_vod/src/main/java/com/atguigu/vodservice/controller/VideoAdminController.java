package com.atguigu.vodservice.controller;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.commonutils.R;
import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.vodservice.util.AliyunVodSDKUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(description = "视频管理")
@RestController
@RequestMapping("/eduvod/video")
@CrossOrigin
public class VideoAdminController {

    @ApiOperation(value = "上传视频")
    @PostMapping("uploadVideo")
    public R uploadVideo(MultipartFile file) {

        try {
            //1上传文件名
            String filename = file.getOriginalFilename();
            //2阿里云显示的名称
            String title = filename.substring(0, filename.lastIndexOf("."));
            //3获取文件流
            InputStream inputStream = file.getInputStream();
            //4创建请求
            UploadStreamRequest request = new UploadStreamRequest(
                    "LTAI3buexRAagkdy", "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz",
                    title, filename, inputStream);

            //5创建对象，提交请求，获得响应
            UploadVideoImpl uploadVideo = new UploadVideoImpl();
            UploadStreamResponse response = uploadVideo.uploadStream(request);
            //6从响应中获取视频id
            String videoId = "";
            if (response.isSuccess()) {
                videoId = response.getVideoId();
            } else {
                videoId = response.getVideoId();
            }

            return R.ok().data("videoId", videoId);
        } catch (IOException e) {
            throw new GuliException(20001, "上传失败");
        }


    }

    @ApiOperation(value = "删除视频")
    @DeleteMapping("{videoId}")
    public R deleteVideoAliyun(@PathVariable String videoId) {

        try {
            //1创建初始化对象
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient("LTAI3buexRAagkdy", "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz");
            //2创建删除视频的请求
            DeleteVideoRequest request = new DeleteVideoRequest();
            //3向请求中设置视频id，支持传入多个视频ID，多个用逗号分隔
            request.setVideoIds(videoId);
            //4调用初始化对象方法
            client.getAcsResponse(request);
            return R.ok();

        } catch (ClientException e) {
            return R.error();
        }
    }

    @ApiOperation(value = "删除多个视频")
    @DeleteMapping("deleteMoreVideo")
    public R deleteMoreVideo (@RequestParam("videoIdList")List<String> videoIdList) {

        try {
            //1创建初始化对象
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient("LTAI3buexRAagkdy", "A6hpWJbF3Zz6wj3jxuBe40Mwryt1Zz");
            //2创建删除视频的请求
            DeleteVideoRequest request = new DeleteVideoRequest();
            //3向请求中设置视频id，支持传入多个视频ID，多个用逗号分隔
            //videoIdList修改成  字符串例如11，22，33格式
            String videoIds = StringUtils.join(videoIdList.toArray(), ",");
            request.setVideoIds(videoIds);
            //4调用初始化对象方法
            client.getAcsResponse(request);
            return R.ok();

        } catch (ClientException e) {
            return R.error();


        }
    }


    @ApiOperation(value = "根据视频id获取视频播放凭证")
    @GetMapping("getPlayAuth/{vid}")
    public R getPlayAuth(@PathVariable String vid){
        //1获取初始化对象
        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient("LTAI4GJPVfxa8f8ifkhubHPn", "LxVJ72WvpmUlM7lgMHJbY824cJX3vD");
            //2 创建request、response对象
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            GetVideoPlayAuthResponse response = new GetVideoPlayAuthResponse();
            //3 向request里设置视频id
            request.setVideoId(vid);

            request.setAuthInfoTimeout(200L);
            //4 调用初始化方法实现功能
            response = client.getAcsResponse(request);
            //5 从response对象中获取内容
            String playAuth = response.getPlayAuth();
            return R.ok().data("playAuth",playAuth);
        } catch (ClientException e) {
            throw new GuliException(20001,"获取视频凭证失败");
        }

    }

}
