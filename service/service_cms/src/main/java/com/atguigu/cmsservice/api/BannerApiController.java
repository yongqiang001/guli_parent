package com.atguigu.cmsservice.api;

import com.atguigu.commonutils.R;
import com.atguigu.cmsservice.entity.Banner;
import com.atguigu.cmsservice.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(description = "banner前台展示")
@RestController
@RequestMapping("/cmsservice/bannerapi")
@CrossOrigin
public class BannerApiController {

    @Autowired
    private BannerService bannerService;

    @ApiOperation(value = "前台查询banner")
    @GetMapping("getAllBanner")

    public R getAllBanner(){
        List<Banner> list = bannerService.getAllBanner();
        return R.ok().data("list", list);
    }
}
