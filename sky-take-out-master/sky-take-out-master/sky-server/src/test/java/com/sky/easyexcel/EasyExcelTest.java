package com.sky.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sky.config.EasyExcelDataListener;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

//webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT 测试环境模拟websocket，否则测试无法启动
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class EasyExcelTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    public void test(){
        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        String templateFileName =EasyExcelTest.class.getResource("/template/category_template.xlsx").getPath();

        List<Category> categoryList = categoryService.list(null);

        // 方案1 一下子全部放到内存里面 并填充
        String fileName = "d:/categoryList.xlsx";
        // 这里 会填充到第一个sheet， 然后文件流会自动关闭
        EasyExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(categoryList);
    }

    @Test
    public void test2(){
        // 模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        String templateFileName =EasyExcelTest.class.getResource("/template/category_template.xlsx").getPath();

        // 方案2 分多次 填充 会使用文件缓存（省内存） jdk8
        // since: 3.0.0-beta1
        String fileName = "d:/categoryList.xlsx";
        // 这里 会填充到第一个sheet， 然后文件流会自动关闭
        try (ExcelWriter excelWriter = EasyExcel.write(fileName).withTemplate(templateFileName).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            for (int i = 1; i <= 3 ; i++) {
                CategoryPageQueryDTO queryDTO = new CategoryPageQueryDTO();
                queryDTO.setPage(i);
                queryDTO.setPageSize(2);
                PageResult page = categoryService.pageQuery(queryDTO);
                List<Category> records = page.getRecords();
                excelWriter.fill(records, writeSheet);
            }
        }
    }


    /**
     * 读取excel
     */
    @Test
    public void indexOrNameRead() {
        //String fileName = EasyExcelTest.class.getResource("/template/categoryList.xlsx").getPath();
        String fileName = "D:\\categoryList.xlsx";
        // 这里默认读取第一个sheet
        EasyExcel.read(fileName, Category.class, new EasyExcelDataListener(categoryService)).sheet().doRead();
    }
}
