package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.service.VodClient;
import com.atguigu.commonutils.R;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VodFileDegradeFeignClient implements VodClient {

    @Override
    public R deleteVideoAliyun(String videoId) {
        return R.error().message("time out");
    }

    @Override
    public R deleteMoreVideo(List<String> videoIdList) {
        return R.error().message("time out");
    }
}
