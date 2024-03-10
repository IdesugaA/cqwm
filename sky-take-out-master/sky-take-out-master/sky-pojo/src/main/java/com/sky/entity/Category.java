package com.sky.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    //类型 1 菜品分类 2 套餐分类
    @ExcelProperty("类别类型")
    private Integer type;

    //分类名称
    @ExcelProperty("类别名字")
    private String name;

    //顺序
    @ExcelProperty("排序")
    private Integer sort;

    //创建时间
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    //更新时间
    @ExcelProperty("修改时间")
    private LocalDateTime updateTime;


    //分类状态 0标识禁用 1表示启用
    private Integer status;

    //创建人
    private Long createUser;

    //修改人
    private Long updateUser;
}
