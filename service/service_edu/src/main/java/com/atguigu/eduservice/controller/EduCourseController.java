package com.atguigu.eduservice.controller;


import com.atguigu.eduservice.entity.vo.CourseInfoFrom;
import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2020-06-26
 */
@Api(description = "课程管理")
@RestController
@RequestMapping("/eduservice/educourse")
@CrossOrigin
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    @ApiOperation(value = "添加课程信息")
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoFrom courseInfoFrom){
       String courseId = courseService.addCourseInfo(courseInfoFrom);
        return R.ok().data("courseId", courseId);
    }


    @ApiOperation(value = "根据课程Id查询课程信息")
    @GetMapping("{courseId}")
    public R getCourseInfoId(@PathVariable String courseId){

        CourseInfoFrom courseInfoFrom = courseService.getCourseInfoId(courseId);
        return R.ok().data("courseInfo", courseInfoFrom);
    }

    @ApiOperation(value = "修改课程信息")
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoFrom courseInfoFrom){
        courseService.updateCourseInfo(courseInfoFrom);
        return  R.ok();

    }

    @ApiOperation(value = "根据课程ID查询课程发布信息")
    @GetMapping("getCoursePulishVoById/{courseId}")
    public R getCoursePulishVoById(@PathVariable String courseId){
        CoursePublishVo coursePublishVo = courseService.getCoursePulishVoById(courseId);
        return R.ok().data("coursePublishVo", coursePublishVo);
    }


    @ApiOperation(value = "发布课程信息")
    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        //根据Id查询课程信息
        EduCourse eduCourse = courseService.getById(id);
        //把status字段改成Normal
        eduCourse.setStatus("Normal");

        courseService.updateById(eduCourse);

        return  R.ok();




    }

    @ApiOperation(value = "课程列表查询")
    @GetMapping("getList/{page}/{limit}")
    //TODO 完善条件查询和分页
    public R getList(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "eduCourse", value = "查询对象", required = false)
                    EduCourse eduCourse){
        Page<EduCourse> pageParam = new Page<>(page, limit);
        courseService.getList(pageParam, eduCourse);

        List<EduCourse> records = pageParam.getRecords();
        long total = pageParam.getTotal();
        return R.ok().data("total", total).data("rows", records );
    }

    @ApiOperation(value = "删除课程（以及相关信息）")
    @DeleteMapping("{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        courseService.deleteCourse(courseId);
        return R.ok();
    }
}

