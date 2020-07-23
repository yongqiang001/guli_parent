package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.ExcelSubjectData;
import com.atguigu.eduservice.entity.vo.OneSubjectVo;
import com.atguigu.eduservice.entity.vo.TwoSubjectVo;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author testjava
 * @since 2020-06-24
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    @Override
    public void importSubjectData(MultipartFile file, EduSubjectService subjectService) {

        //获取文件的输入流
        try {
            InputStream in = file.getInputStream();
            EasyExcel.read(in, ExcelSubjectData.class, new SubjectExcelListener(subjectService)).sheet().doRead();



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //课程分类
    @Override
    public List<OneSubjectVo> getAllSubject() {
        //1.获取一级分类数据
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id","0");
        List<EduSubject> oneSubjectList = baseMapper.selectList(wrapperOne);
        //2.获取二级分类数据
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.ne("parent_id","0");
        List<EduSubject> twoSubjectList = baseMapper.selectList(wrapperTwo);
        //3.创建最终返回的数据集合
        List<OneSubjectVo> finalSubjectList = new ArrayList<>();


        //4.封装一级分类
        //4.1遍历一级分类
        for (int i = 0; i < oneSubjectList.size() ; i++) {
            EduSubject oneSubject = oneSubjectList.get(i);
            //4.2EduSubject转化成OneSubjectVo
            OneSubjectVo oneSubjectVo = new OneSubjectVo();
//            oneSubject.setId(oneSubject.getId());
//            oneSubject.setTitle(oneSubject.getTitle());

            //把oneSubject里的值复制到oneSubjectVo
            BeanUtils.copyProperties(oneSubject,oneSubjectVo);

            //4.3把数据存入到finalSubjectList
            finalSubjectList.add(oneSubjectVo);

            //4.4创建集合用于封装二级
            List<TwoSubjectVo> twoVoList = new ArrayList<>();

            //5.封装二级分类
            for (int m = 0; m < twoSubjectList.size() ; m++) {
                EduSubject twoSubject = twoSubjectList.get(m);
                //5.1判断，判断一级分类id和二级分类pid比较
                if (oneSubject.getId().equals(twoSubject.getParentId())){
                    //5.2类型转换  把twoSubject转化成TwoSubjectVo
                    TwoSubjectVo twoSubjectVo = new TwoSubjectVo();
                    BeanUtils.copyProperties(twoSubject, twoSubjectVo);
                    twoVoList.add(twoSubjectVo);
                }
            }

            //6.二级Vo集合存入遗迹Vo里
            oneSubjectVo.setChildren(twoVoList);

        }



        return finalSubjectList;
    }
}
