package com.own.service.impl;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.own.dto.GoodsSalesDTO;
import com.own.entity.Orders;
import com.own.mapper.OrderMapper;
import com.own.mapper.UserMapper;
import com.own.service.ReportService;
import com.own.service.WorkspaceService;
import com.own.vo.*;
import org.apache.commons.lang3.StringUtils;
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

@Service
public class ReportServiceImpl implements ReportService {

	@Resource
	private OrderMapper orderMapper;

	@Resource
	private UserMapper userMapper;

	@Resource
	private WorkspaceService workspaceService;

	//营业额统计
	@Override
	public TurnoverReportVO turnoverStatistics(LocalDate begin , LocalDate end){

		//计算出指定开始日期到结束日期的日期列表集合List<LocalDate>
		//定义日期集合对象，收集每天日期
		List<LocalDate> dateList = getDateList(begin,end);
		//计算出每一天的营业额数据列表List<Double>
		//定义集合，收集每天营业额数据
		ArrayList<Double> turnoverList = new ArrayList<>();
		//遍历dateList日期列表，计算每一天营业额数据
		dateList.forEach(date -> {
			//计算当天最小时间
			LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);
			//计算当天最大时间
			LocalDateTime endTime = LocalDateTime.of(date , LocalTime.MAX); //年-月-日 23:59:59.9999999
						
			//调用orderMapper查询当天的已完成状态的营业额数据
			Map map = new HashMap();
			map.put("status", Orders.COMPLETED);
			map.put("beginTime",beginTime);
			map.put("endTime",endTime);
			Double sum = orderMapper.sumByMap(map);
			if(sum==null){
				sum=0.0;
			}
			turnoverList.add(sum);
			
		});

		//封装TurnoverReportVO数据返回
		//List<LOcalDate>日期列表转换为字符串，日期之间用逗号隔开
		String dateListStr = StringUtils.join(dateList,",");
		//List<Double>日期列表转换为字符串，数据之间用逗号隔开
		String turnoverListStr = StringUtils.join(turnoverList,",");
		return TurnoverReportVO.builder()
			.dateList(dateListStr)
			.turnoverList(turnoverListStr)
			.build();
	}

	//用户统计业务方法
	@Override
	public UserReportVO userStatistics(LocalDate begin , LocalDate end){

		List<LocalDate> dateList = getDateList(begin,end);
		//计算出每一天的新增用户数和用户总数数据列表
		ArrayList<Integer> newUserList = new ArrayList<>();
		ArrayList<Integer> totalUserList = new ArrayList<>();
		//获取开始日期前一天最大时间
		LocalDateTime beforeEndTime = LocalDateTime.of(begin.minusDays(1),LocalTime.MAX);
		Map map = new HashMap();
		map.put("endTime",beforeEndTime);
		//获取前一天用户总数
		Integer total = userMapper.countByMap(map);//通过map得到
		if(ObjectUtils.isEmpty(total)){
			total = 0;
		}
		//遍历List<LocalDate>日期列表，计算每一天的新增用户数和用户总数，并添加到集合中
		for(LocalDate date : dateList){
			//计算当天最小时间
			LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN) //年-日-月 00::00
										 //计算当天最大时间
			LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
			//封装查询条件
			map.put("beginTime",beginTime);
			map.put("endTime",endTime);
			//执行查询当天的新增用户数
			Integer newUserCount = userMapper.countByMap(map);
			newUserList.add(newUserCount);
			//计算当天的用户总数=前一天总数+当天新增用户数
			//前一天总数+当天新增的总数
			total += newUserCount;
			totalUserList.add(total);
		}
		return UserReportVO.builder()
			.dateList(StringUtils.join(dateList,","))
			.newUserList(StringUtils.join(newUserList,","))
			.totalUserList(StringUtils.join(totalUserList,","))
			.build();
	}

	//根据开始日期和结束日期计算日期列表
	private List<LocalDate> getDateList(LocalDate begin , LocalDate end){

		//定义日期集合对象，收集每天日期
		List<LocalDate> dateList = new ArrayList<>();
		dateList.add(begin);
		//通过循环，给begin开始日期不断+1,直到最后日期end结束，得到每一天的日期数据
		while(!begin.equals(end)){
			begin = begin.plusDays(1);
			dateList.add(begin);
		}
		return dateList;
	}

	//订单统计业务方法
	@Override
	public OrderReportVO ordersStatistics(LocalDate begin , LocalDate end){

		List<LocalDate> dateList = getDateList(begin,end);
		//计算出每一天的有效订单数列表和订单总数列表
		//定义有效订单数列表
		List<Integer> validOrderCountList = new ArrayList();
		//定义订单数列表
		List<Integer> orderCountList = new ArrayList();
		//定义订单总数
		Integer totalOrderCount = 0;

		//定义有效订单总数
		Integer validOrderCount = 0;

		//遍历集合List<LocalDate>，计算每一天的订单数
		for(LocalDate date : dateList){
			LocalDateTime beginTime = LocalDateTime.of(date,LocalTime.MIN);//年-月-日 00:00
			LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);//年-月-日 23:59:59.999999
			//获取当天的订单总数
			Map map = new HashMap();
			map.put("beginTime",beginTime);
			map.put("endTime",endTime);
			Integer totalByDay = orderMapper.countByMap(map);
			orderCountList.add(totalByDay);
			//累加总订单数
			totalOrderCount += totalByDay
			
			//获取当前的有效订单总数
			map.put("status",Orders.COMPLETED);
			Integer validCountByDay = orderMapper.countByMap(map);
			validOrderCount += validCountByDay;
		}
		//计算订单完成率 = 有效订单总数 / 总订单数
		Double orderCompletionRate = 0.0;
		if(totalOrderCount != 0){
			orderCompletionRate = (validOrderCount * 1.0) / totalOrderCount;
		}

		return OrderReportVO.builder()
			.totalOrderCount(totalOrderCount)
			.validOrderCount(validOrderCount)
			.orderCompletionRate(orderCompletionRate)
			.validOrderCountList(StringUtils.join(validOrderCountList,","))
			.orderCountList(StringUtils.join(orderCountList,","))
			.build();

	}

	public SalesTop10ReportVO top10(LocalDate begin , LocalDate end){

		LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
		LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);

		//查询获取销售排行列表数据
		List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime,endTime);
		if(CollectionUtils.isEmpty(goodsSalesDTOList)){
			return null;
		}

		//根据List<GoodsSalesDTO>计算获取里面name收集到List<String>商品名字列表
		List<String> nameList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
		List<Integer> numberList = goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
		return SalesTop10ReportVO.builder()
			.nameList(StringUtils.join(nameList,","))
			.numberList(StringUtils.join(numberList,","))
			.build();
	}

	@Resource
	private HttpServletResponse response;

	@Override
	public void export1(){



	}

	@Override
	public void export2(){
		try(//获取类路径下template/运营数据报表模板.xlsx文件输入流
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/data.xlsx");
			ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(in).build();

		   ){
			WriteSheet writeSheet = EasyExcel.writerSheet().build();

			//计算近30天的概览数据并填充
			LocalDate begin = LocalDate.now().minusDays(30);
			LocalDate end = LocalDate.now().minusDays(1);
			LocalDateTime beginTime = LocalDateTime.of(begin,LocalTime.MIN);
			LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);
			//获取营业额概览数据
			BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginTime,endTime);
			businessDataVO.setDateRange("时间："+begin+"至"+end);
			excelWriter.fill(businessDataVO,writeSheet);

			List<LocalDate> dateList = getDateList(begin,end);
			//遍历日期集合，计算每一天的营业额数据并填充
			int i = 7;
			//定义集合收集每一天营业额数据
			List<BusinessDataVO> businessDataVOList = new ArrayList<>();
			for(LocalDate date: dateList){
				beginTime = LocalDateTime.of(date , LocalTime.MIN);
				endTime = LocalDateTime.of(date,LocalTime.MAX);
				//获取当天营业额数据
				BusinessDataVO businessDataVOByDay = workspaceService.getBusinessData(beginTime,endTime);
				businessDataVOByDay.setDate(date.toString());
				//将businessDataVOByDay添加集合中
				businessDataVOList.add(businessDataVOByDay);
			}
			excelWriter.fill(businessDataVOList,writeSheet);

		   } catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException(e);	
		   }

		//这里使用了try - with - resource的写法

	}




}
