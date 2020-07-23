package com.atguigu.eduservice.api;

import com.atguigu.commonutils.R;
import com.atguigu.commonutils.utils.JwtUtils;
import com.atguigu.commonutils.vo.CourseWebVoPay;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.ChapterVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.entity.vo.CourseWebVo;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.OrderClient;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(description="前台课程展现")
@RestController
@RequestMapping("/eduservice/courseapi")
@CrossOrigin
public class CourseApiController {
    @Autowired
    private EduCourseService courseService;
    @Autowired
    private EduChapterService chapterService;
    @Autowired
    private OrderClient orderClient;

    @ApiOperation(value = "带条件分页查询课程列表")
    @PostMapping("getFrontCourseList/{current}/{limit}")
    public R getCoursePageVo(@PathVariable Long current,
                              @PathVariable Long limit,
                              @RequestBody CourseQueryVo courseQueryVo){
        Page<EduCourse> page = new Page<>(current,limit);
        Map<String,Object> map = courseService.getCoursePageList(page,courseQueryVo);
        return R.ok().data(map);
    }


    @ApiOperation(value = "根据id查询课程详情")
    @GetMapping("getCourseInfo/{id}")
    public R getCourseInfo(@PathVariable String id,
                           HttpServletRequest request){
        //1查询课程详情信息
        CourseWebVo courseWebVo = courseService.getCourseWebVo(id);
        //2查询课程相关大纲数据
        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoById(id);

        //3查询课程是否被购买
        String memberId   = JwtUtils.getMemberIdByJwtToken(request);
        boolean isBuyCourse = orderClient.isBuyCourse(id, memberId );

        System.out.println("memberId:" + memberId );
        return R.ok().data("courseWebVo",courseWebVo)
                .data("chapterVideoList",chapterVideoList)
                .data("isBuyCourse",isBuyCourse);
    }

    @ApiOperation(value = "根据id查询课程、讲师信息，支付订单模块远程调用")
    @GetMapping("getCourseInfoPay/{id}")
    public CourseWebVoPay  getCourseInfoPay(@PathVariable String id){
        //查询课程基本信息
        CourseWebVo courseWebVo = courseService.getCourseWebVo(id);
        CourseWebVoPay courseWebVoPay = new CourseWebVoPay();
        BeanUtils.copyProperties(courseWebVo, courseWebVoPay);
        return courseWebVoPay;

    }
}
