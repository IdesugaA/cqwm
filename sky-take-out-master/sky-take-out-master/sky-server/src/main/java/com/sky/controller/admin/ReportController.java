package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @Description ReportController
 * @Author songyu
 * @Date 2023-10-08
 */
@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "报表相关接口")
public class ReportController {


    @Resource
    private ReportService reportService;


    /**
     * 处理营业额统计报表请求
     * @param begin
     * @param end
     * @return
     *
     * Springmvc默认可以封装时间格式 "yyyy/MM/dd"日期数据,但是前端发送的格式 "yyyy-MM-dd"
     *        需要使用@DateTimeFormat(pattern = "yyyy-MM-dd") 进行格式化封装
     */
    @ApiOperation("营业额统计报表接口")
    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("开始执行营业额统计报表接口：开始日期 {},结束日期 {}",begin,end);

        //调用业务查询统计数据
        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin,end);

        //返回
        return  Result.success(turnoverReportVO);
    }

    /**
     * 处理用户统计报表请求
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("用户统计报表接口")
    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("开始执行用户统计报表接口：开始日期 {},结束日期 {}",begin,end);

        //调用业务查询统计数据
        UserReportVO userReportVO = reportService.userStatistics(begin,end);

        //返回
        return  Result.success(userReportVO);
    }

    /**
     * 处理订单统计报表请求
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("订单统计报表接口")
    @GetMapping("/ordersStatistics")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("开始执行订单统计报表接口：开始日期 {},结束日期 {}",begin,end);

        //调用业务查询统计数据
        OrderReportVO orderReportVO = reportService.ordersStatistics(begin,end);

        //返回
        return  Result.success(orderReportVO);
    }

    /**
     * 处理订单统计报表请求
     * @param begin
     * @param end
     * @return
     */
    @ApiOperation("销售top10统计报表接口")
    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ){
        log.info("开始执行销售top10统计报表接口：开始日期 {},结束日期 {}",begin,end);

        //调用业务查询统计数据
        SalesTop10ReportVO salesTop10ReportVO = reportService.top10(begin,end);

        //返回
        return  Result.success(salesTop10ReportVO);
    }

    /**
     * 处理导出近30天运营数据 请求
     */
    @ApiOperation("导出近30天运营数据报表接口")
    @GetMapping("/export")
    public void export(){

        log.info("开始执行导出近30天运营数据报表接口...");

        //调用业务执行导出数据
        //reportService.export(); //使用poi导出
        reportService.export2(); //使用easyExcel导出
    }

}
