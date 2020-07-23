package com.atguigu.eduservice.excel;

import com.alibaba.excel.EasyExcel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestEasyExcel {

    @Test
    public void writeExcel() {

        // 写法1
        String fileName = "F:\\在线教育项目\\write.xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, DemoData.class).sheet("学生列表").doWrite(data());
        System.out.println("操作完成");
    }



        //循环设置要添加的数据，最终封装到list集合中
        private static List<DemoData> data() {
            List<DemoData> list = new ArrayList<DemoData>();
            for (int i = 0; i < 10; i++) {
                DemoData data = new DemoData();
                data.setSno(i);
                data.setSname("张三"+i);
                list.add(data);
            }
            return list;
        }


    @Test
    public void readExcel(){
        // 写法1：
        String fileName = "F:\\在线教育项目\\write.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, DemoData.class, new ExcelListener()).sheet().doRead();

    }

}
