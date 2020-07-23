package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.CourseInfoFrom;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.entity.vo.CourseWebVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author testjava
 * @since 2020-06-26
 */
public interface EduCourseService extends IService<EduCourse> {

    String addCourseInfo(CourseInfoFrom courseInfoFrom);

    CourseInfoFrom getCourseInfoId(String courseId);

    void updateCourseInfo(CourseInfoFrom courseInfoFrom);

    CoursePublishVo getCoursePulishVoById(String courseId);

    void deleteCourse(String courseId);

    void getList(Page<EduCourse> pageParam, EduCourse eduCourse);

    Map<String, Object> getCoursePageList(Page<EduCourse> pageParam, CourseQueryVo courseQueryVo);

    CourseWebVo getCourseWebVo(String id);
}
