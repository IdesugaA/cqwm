package com.sky.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description ReportServiceImpl
 * @Author songyu
 * @Date 2023-10-08
 */
@Service
public class ReportServiceImpl implements ReportService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private WorkspaceService workspaceService;

    /**
     * 营业额统计业务方法
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {

        //1.计算出指定开始日期到结束日期的日期列表集合List<LocalDate>
        //定义日期集合对象，收集每天日期
        List<LocalDate> dateList = getDateList(begin, end);

        //2.计算出每一天的营业额数据列表List<Double>
        //定义集合，收集每天营业额数据
        ArrayList<Double> turnoverList = new ArrayList<>();
        //遍历dateList日期列表，计算每一天营业额数据
        dateList.forEach(date -> {
            //计算当天最小时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  // 年-月-日 00:00
            //计算当天最大时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  // 年-月-日 23:59:59.999999999

            //调用orderMapper查询当天的已完成状态的营业额数据
            Map map = new HashMap();
            map.put("status", Orders.COMPLETED);
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            Double sum = orderMapper.sumByMap(map);
            if(sum==null){
                sum=0.0;
            }
            turnoverList.add(sum);
        });

        //3.封装TurnoverReportVO数据返回
        //List<LocalDate> 日期列表转换为字符串，日期之间逗号隔开
        String dateListStr = StringUtils.join(dateList, ",");
        //List<Double> 日期列表转换为字符串，数据之间逗号隔开
        String turnoverListStr = StringUtils.join(turnoverList, ",");
        return TurnoverReportVO.builder()
                .dateList(dateListStr)
                .turnoverList(turnoverListStr)
                .build();
    }

    /**
     * 用户统计业务方法
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //1.计算出指定开始日期到结束日期的日期列表集合List<LocalDate>
        List<LocalDate> dateList = getDateList(begin, end);

        //2.计算出每一天的新增用户数据和用户总数数据列表List<Integer>
        //定义新增用户数列表List<Integer>
        ArrayList<Integer> newUserList = new ArrayList<>();
        //定义用户总数列表List<Integer>
        ArrayList<Integer> totalUserList = new ArrayList<>();
        //获取开始日期前一天最大时间
        LocalDateTime beforeEndTime = LocalDateTime.of(begin.minusDays(1), LocalTime.MAX);
        Map map = new HashMap();
        map.put("endTime",beforeEndTime);
        //获取前一天用户总数
        Integer total = userMapper.countByMap(map);
        if(ObjectUtils.isEmpty(total)){
            total = 0;
        }
        //遍历List<LocalDate>日期列表，计算每一天的新增用户数和用户总数，并添加到集合中
        for (LocalDate date : dateList) {
            //计算当天最小时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  // 年-月-日 00:00
            //计算当天最大时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  // 年-月-日 23:59:59.999999999
            //封装查询条件
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            //执行查询当天的新增用户数
            Integer newUserCount = userMapper.countByMap(map);
            newUserList.add(newUserCount);
            //计算当天的用户总数=前一天总数+当天新增用户数
            //    大纲的方案：每一天都执行sql获取
            //         map.put("beginTime",null);
            //         total = userMapper.countByMap(map);
            //    我们的方案（不用执行sql,更优）：前一天的总数+当天新增的总数
            total += newUserCount;
            totalUserList.add(total);
        }

        //3.封装UserReportVO数据返回
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }

    /**
     * 根据开始日期与结束日期计算日期列表返回
     * @param begin
     * @param end
     * @return
     */
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {

        //定义日期集合对象，收集每天日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        //通过循环，给begin开始日期不断+1,直到最后日期end结束，得到每一天的日期数据
        while (!begin.equals(end)){
            begin =  begin.plusDays(1);
            dateList.add(begin);
        }
        return dateList;
    }

    /**
     * 订单统计业务方法
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //1.计算出指定开始日期到结束日期的日期列表集合List<LocalDate>
        List<LocalDate> dateList = getDateList(begin, end);

        //2.计算出每一天的有效订单数列表和订单总数数列表List<Integer>
        //定义有效订单数列表
        List<Integer> validOrderCountList = new ArrayList();
        //定义订单数列表
        List<Integer> orderCountList = new ArrayList();
        //定义订单总数
        Integer totalOrderCount = 0;
        //定义有效订单总数
        Integer validOrderCount = 0;

        //3.遍历集合List<LocalDate>，计算每一天的订单数
        for (LocalDate date : dateList) {
            //计算当天最小时间
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  // 年-月-日 00:00
            //计算当天最大时间
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);  // 年-月-日 23:59:59.999999999
            //获取当天的订单总数
            Map map = new HashMap();
            map.put("beginTime",beginTime);
            map.put("endTime",endTime);
            Integer totalByDay = orderMapper.countByMap(map);
            orderCountList.add(totalByDay);
            //累加总订单数
            totalOrderCount += totalByDay;

            //获取当前的有效订单总数
            map.put("status",Orders.COMPLETED);
            Integer validCountByDay = orderMapper.countByMap(map);
            validOrderCountList.add(validCountByDay);
            //累加有效订单总数
            validOrderCount += validCountByDay;
        }

        //4.计算订单完成率 = 有效订单总数/总订单数
        Double orderCompletionRate = 0.0;
        if(totalOrderCount!=0){
            orderCompletionRate =  (validOrderCount * 1.0) / totalOrderCount;
        }

        //5.封装数据OrderReportVO返回
        return OrderReportVO.builder()
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .dateList(StringUtils.join(dateList,","))
                .build();
    }

    /**
     * 销售top10方法
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {

        //1.计算开始日期最小时间
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);  // 年-月-日 00:00
        //2.计算结束日期最大时间
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);  // 年-月-日 23:59:59.999999999

        //3.执行查询获取销售排行列表数据List<GoodsSalesDTO>
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime,endTime);
        if(CollectionUtils.isEmpty(goodsSalesDTOList)){
            return null;
        }

        //4.根据List<GoodsSalesDTO>计算获取里面name收集到List<String>商品名字列表
        List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        //5.根据List<GoodsSalesDTO>计算获取里面number收集到List<Integer>商品名字列表
        List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        //6.封装数据SalesTop10ReportVO返回
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    @Resource
    private HttpServletResponse response;

    /**
     * 导出近30天运营数据
     */
    @Override
    public void export() {
        try (   //1.获取类路径下template/运营数据报表模板.xlsx文件输入流
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

                //2.根据输入流创建内存POI的工作薄
                XSSFWorkbook workbook = new XSSFWorkbook(in);){

            //获取工作表
            XSSFSheet sheet = workbook.getSheetAt(0);

            //3.计算近30天的概览数据并填充
            LocalDate begin = LocalDate.now().minusDays(30);
            LocalDate end = LocalDate.now().minusDays(1);
            //计算开始日期最小时间和结束日期最大时间
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
            //获取营业额概览数据
            BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);
            //填充数据-时间范围： 行下标1，单元格下标1， 格式“时间：开始日期至结束日期”
            sheet.getRow(1).getCell(1).setCellValue("时间："+begin+"至"+end);
            //填充数据-营业额：  行下标3，单元格下标2
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            //填充数据-订单完成率：  行下标3，单元格下标4
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            //填充数据-新增用户数：  行下标3，单元格下标6
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            //填充数据-有效订单数：  行下标4，单元格下标2
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            //填充数据-平均客单价：  行下标4，单元格下标4
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());


            //4.计算每一天的概览数据并填充
            //根据开始日期和结束日期获取日期列表List<LocalDate>
            List<LocalDate> dateList = getDateList(begin, end);
            //遍历日期集合，计算每一天的营业额数据并填充
            int i = 7;
            for (LocalDate date : dateList) {
                //计算当天最小时间和当天最大时间
                beginTime = LocalDateTime.of(date, LocalTime.MIN);
                endTime = LocalDateTime.of(date, LocalTime.MAX);
                //获取当天营业额数据
                BusinessDataVO businessDataVOByDay = workspaceService.getBusinessData(beginTime, endTime);

                XSSFRow row = sheet.getRow(i);
                //填充数据-日期：  行下标i，单元格下标1
                row.getCell(1).setCellValue(date.toString());
                //填充数据-营业额：  行下标i，单元格下标2
                row.getCell(2).setCellValue(businessDataVOByDay.getTurnover());
                //填充数据-有效订单数：  行下标i，单元格下标3
                row.getCell(3).setCellValue(businessDataVOByDay.getValidOrderCount());
                //填充数据-订单完成率：  行下标i，单元格下标4
                row.getCell(4).setCellValue(businessDataVOByDay.getOrderCompletionRate());
                //填充数据-平均客单价：  行下标i，单元格下标5
                row.getCell(5).setCellValue(businessDataVOByDay.getUnitPrice());
                //填充数据-新增用户数：  行下标i，单元格下标6
                row.getCell(6).setCellValue(businessDataVOByDay.getNewUsers());

                i++;
            }

            //5.输出工作薄输出给浏览器输出流让用户下载
            workbook.write(response.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 导出近30天运营数据,使用easyexcel实现
     */
    @Override
    public void export2() {
        try (   //1.获取类路径下template/运营数据报表模板.xlsx文件输入流
                InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/data.xlsx");
                ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(in).build();
                ){

            WriteSheet writeSheet = EasyExcel.writerSheet().build();

            //3.计算近30天的概览数据并填充
            LocalDate begin = LocalDate.now().minusDays(30);
            LocalDate end = LocalDate.now().minusDays(1);
            //计算开始日期最小时间和结束日期最大时间
            LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
            //获取营业额概览数据
            BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime, endTime);
            businessDataVO.setDateRange("时间："+begin+"至"+end);
            excelWriter.fill(businessDataVO, writeSheet);

            //4.计算每一天的概览数据并填充
            //根据开始日期和结束日期获取日期列表List<LocalDate>
            List<LocalDate> dateList = getDateList(begin, end);
            //遍历日期集合，计算每一天的营业额数据并填充
            int i = 7;
            //定义集合收集每一天营业额数据
            List<BusinessDataVO> businessDataVOList = new ArrayList<>();
            for (LocalDate date : dateList) {
                //计算当天最小时间和当天最大时间
                beginTime = LocalDateTime.of(date, LocalTime.MIN);
                endTime = LocalDateTime.of(date, LocalTime.MAX);
                //获取当天营业额数据
                BusinessDataVO businessDataVOByDay = workspaceService.getBusinessData(beginTime, endTime);
                businessDataVOByDay.setDate(date.toString());
                //将businessDataVOByDay添加集合中
                businessDataVOList.add(businessDataVOByDay);

            }
            excelWriter.fill(businessDataVOList, writeSheet);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
