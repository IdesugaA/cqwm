package com.sky.config;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;

import com.sky.entity.Category;
import com.sky.service.CategoryService;
import com.sky.service.impl.CategoryServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class EasyExcelDataListener implements ReadListener<Category> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;

    /**
     * 缓存的数据
     */
    private List<Category> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 假设这个是一个Service，当然有业务逻辑这个也可以是一个Mapper。当然如果不用存储这个对象没用。
     */
    private CategoryService categoryService;

    public EasyExcelDataListener() {
        // 这里是demo，所以随便new一个。实际使用如果到了spring,请使用下面的有参构造函数
        categoryService = new CategoryServiceImpl();
    }

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public EasyExcelDataListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     *  这个每一条数据解析都会来调用
     * @param category
     * @param analysisContext
     */
    @Override
    public void invoke(Category category, AnalysisContext analysisContext) {
        log.info("解析到一条数据:{}", JSON.toJSONString(category));
        cachedDataList.add(category);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", cachedDataList.size());
        //调用 categoryService.add(cachedDataList) 批量吸入数据库
        log.info("存储数据库成功！");
    }
}