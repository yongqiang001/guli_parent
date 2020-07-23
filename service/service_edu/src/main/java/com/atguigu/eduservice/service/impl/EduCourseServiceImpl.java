package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduChapter;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.vo.CourseInfoFrom;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQueryVo;
import com.atguigu.eduservice.entity.vo.CourseWebVo;
import com.atguigu.eduservice.mapper.EduCourseMapper;

import com.atguigu.servicebase.handler.GuliException;
import com.atguigu.eduservice.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-06-26
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    @Autowired
    private EduCourseDescriptionService courseDescriptionService;
    @Autowired
    private EduVideoService videoService;
    @Autowired
    private EduChapterService chapterService ;
    @Autowired
    private VodClient vodClient;

    @Override
    public String addCourseInfo(CourseInfoFrom courseInfoFrom) {
        //1添加课程信息
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoFrom,eduCourse);
        int insert = baseMapper.insert(eduCourse);
        if(insert==0){//添加课程信息失败
            throw new GuliException(20001,"添加课程信息失败");
        }
        //2获取添加课程后的主键id
        String courseId = eduCourse.getId();

        //3添加课程描述信息
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseId);
        courseDescription.setDescription(courseInfoFrom.getDescription());
        courseDescriptionService.save(courseDescription);


        return courseId;
    }


    //根据Id查询课程信息
    @Override
    public CourseInfoFrom getCourseInfoId(String courseId) {
        //1.查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        //1.1  eduCourse复制到CourseInfoFrom
        CourseInfoFrom courseInfoFrom = new CourseInfoFrom();
        BeanUtils.copyProperties(eduCourse, courseInfoFrom);


        //2.查询课程详情表
        EduCourseDescription courseDescription = courseDescriptionService.getById(courseId);
        courseInfoFrom.setDescription(courseDescription.getDescription());



        return courseInfoFrom;
    }

    @Override
    public void updateCourseInfo(CourseInfoFrom courseInfoFrom) {
        //1.修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoFrom,eduCourse);
        int update = baseMapper.updateById(eduCourse);

        if (update==0){
            throw new GuliException(20001,"修改课程表信息失败");

        }

        //2.修改课程详情表
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setId(courseDescription.getId());
        courseDescription.setDescription(courseInfoFrom.getDescription());
        courseDescriptionService.updateById(courseDescription);
    }

    //根据课程ID查询课程发布信息
    @Override
    public CoursePublishVo getCoursePulishVoById(String id) {
        //1.查询课程表
        CoursePublishVo coursePublishVo = baseMapper.getCoursePulishVoById(id);

        return coursePublishVo;
    }


    //删除课程
    @Override
    public void deleteCourse(String courseId) {


        //1根据课程id删除小节
        //1.1根据课程ID查询该课程所有的视频
        QueryWrapper<EduVideo> wrapperVideoId = new QueryWrapper<>();
        wrapperVideoId.eq("course_id",courseId);
        List<EduVideo> list = videoService.list(wrapperVideoId);

        //1.2.封装需要删除的视频ID
        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            //1.3 从小节信息里获取视频ID
            EduVideo eduVideo = list.get(i);
            String videoSourceId = eduVideo.getVideoSourceId();

            //1.4 判断后存入集合
            if (!StringUtils.isEmpty(videoSourceId)){
                videoIds.add(videoSourceId);

            }
        }

        //1.5 判断后调用接口删除多个视频
        if (videoIds.size() > 0){
            vodClient.deleteMoreVideo(videoIds);

        }




        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id", courseId);
        videoService.remove(wrapperVideo);

        //根据课程Id删除章节
        QueryWrapper<EduChapter> wrapperChapter = new QueryWrapper<>();
        wrapperChapter.eq("course_id", courseId);
        chapterService.remove(wrapperChapter);

        //3根据课程id删除描述
        courseDescriptionService.removeById(courseId);

        //4根据课程id删除课程
        int delete = baseMapper.deleteById(courseId);
        if (delete==0){
            throw new GuliException(20001,"删除课程失败");

        }


    }

    @Override
    public void getList(Page<EduCourse> pageParam, EduCourse eduCourse) {


        QueryWrapper<EduCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("gmt_create");
        if (eduCourse == null){
            baseMapper.selectPage(pageParam,queryWrapper);

            return;
        }
        String title =eduCourse.getTitle();
        String teacherId = eduCourse.getTeacherId();
        String subjectParentId = eduCourse.getSubjectParentId();
        String subjectId = eduCourse.getSubjectId();

        if (!StringUtils.isEmpty(title)){
            queryWrapper.like("title", title);
        }
        if (!StringUtils.isEmpty(teacherId)){
            queryWrapper.like("teacher_id", teacherId);
        }
        if (!StringUtils.isEmpty(subjectParentId)){
            queryWrapper.like("subject_parent_id", subjectParentId);
        }
        if (!StringUtils.isEmpty(subjectId)){
            queryWrapper.like("subject_id", subjectId);
        }
        baseMapper.selectPage(pageParam, queryWrapper);
    }

    @Override
    public Map<String, Object> getCoursePageList(Page<EduCourse> pageParam, CourseQueryVo courseQueryVo) {

        //1取出查询条件
        String subjectParentId = courseQueryVo.getSubjectParentId();//一级分类
        String subjectId = courseQueryVo.getSubjectId();//二级分类
        String buyCountSort = courseQueryVo.getBuyCountSort();//关注度
        String priceSort = courseQueryVo.getPriceSort();//价格
        String gmtCreateSort = courseQueryVo.getGmtCreateSort();//时间

        //2条件判断
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(subjectParentId)){
            wrapper.eq("subject_parent_id",subjectParentId);
        }
        if(!StringUtils.isEmpty(subjectId)){
            wrapper.eq("subject_id",subjectId);
        }
        if(!StringUtils.isEmpty(buyCountSort)){
            wrapper.orderByDesc("buy_count");
        }
        if(!StringUtils.isEmpty(priceSort)){
            wrapper.orderByDesc("price");
        }
        if(!StringUtils.isEmpty(gmtCreateSort)){
            wrapper.orderByDesc("gmt_create");
        }
        //3查询数据
        baseMapper.selectPage(pageParam,wrapper);
        //4封装数据
        List<EduCourse> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();
        boolean hasPrevious = pageParam.hasPrevious();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;
    }

    @Override
    public CourseWebVo getCourseWebVo(String id) {
        CourseWebVo courseWebVo = baseMapper.getFrontCourseInfo(id);
        return courseWebVo;
    }


}
