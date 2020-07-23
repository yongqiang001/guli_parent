package com.atguigu.eduservice.service;

import com.atguigu.commonutils.R;
import com.atguigu.eduservice.service.impl.VodFileDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "service-vod", fallback = VodFileDegradeFeignClient.class)
@Component
public interface VodClient {

    // url 必须是完整的
    @DeleteMapping("/eduvod/video/{videoId}")
    public R deleteVideoAliyun(@PathVariable("videoId") String videoId);



    @DeleteMapping("/eduvod/video/deleteMoreVideo")
    public R deleteMoreVideo (@RequestParam("videoIdList") List<String> videoIdList);
}
