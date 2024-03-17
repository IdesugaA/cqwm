package com.own.service.impl;


import com.own.constant.StatusConstant;
import com.own.entity.Orders;
import com.own.mapper.DishMapper;
import com.own.mapper.OrderMapper;
import com.own.mapper.SetmealMapper;
import com.own.mapper.UserMapper;
import com.own.service.WorkspaceService;
import com.own.vo.BusinessDataVO;
import com.own.vo.DishOverViewVO;
import com.own.vo.OrderOverViewVO;
import com.own.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private DishMapper dishMapper;

	@Autowired
	private SetmealMapper setmealMapper;

	//根据时间段统计营业数据
	@Override
	public BusinessDataVO getBusinessData(LocalDateTime begin , LocalDateTime end){

		/**
		 *
		 *	营业额：当日已完成订单的总金额
		 *	有效订单：当然已完成的数量
		 *	订单完成率：有效订单数/总订单数
		 *	平均客单价：营业额/有效订单数
		 *	新增用户：当日新增用户的数量
		 *
		 *
		 *
		 *
		 */
		Map map = new HashMap();
		map.put("beginTime",begin);
		map.put("endTime",end);

		//查询总订单数
		Integer totalOrderCount = orderMapper.countByMap(map);
		map.put("status", Orders.COMPLETED);

		//营业额
		Double turnover = orderMapper.sumByMap(map);
		turnover = turnover == null ? 0.0 : turnover;
		//有效订单数
		Integer validOrderCount = orderMapper.countByMap(map);
		Double unitPrice = 0.0;
		Double orderCompletionRate = 0.0;
		
		if(totalOrderCount != 0 && validOrderCount != 0){

			//订单完成率
			orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;

			//平均单客价
			unitPrice = turnover / validOrderCount;
		}
		
		Integer newUsers = userMapper.countByMap(map);
		return BusinessDataVO.builder()
			.turnover(turnover)
			.validOrderCount(validOrderCount)
			.orderCompletionRate(orderCompletionRate)
			.unitPrice(unitPrice)
			.newUsers(newUsers)
			.build();
		

	}


	@Override
	//查询订单管理数据
	public OrderOverViewVO gerOrderOverView(){

		Map map = new HashMap();
		map.put("begin",LocalDateTime.now().with(LocalTime.MIN));
		map.put("status",Orders.TO_BE_CONFIRMED);
		//待接单
		Integer waitingOrders = orderMapper.countByMap(map);
		//待派送
		map.put("status",Orders.CONFIRMED);
		Integer deliverdOrders = orderMapper.countByMap(map);
		//已完成
		map.put("status",Orders.COMPLETED);
		Integer completedOrders = orderMapper.countByMap(map);
		//已取消
		map.put("status",Orders.CANCELLED);
		Integer cancelledOrders = orderMapper.countByMap(map);

		//全部订单
		map.put("status",null);
		Integer allOrders = orderMapper.countByMap(map);
		return OrderOverViewVO.builder()
			.waitingOrders(waitingOrders)
			.deliveredOrders(deliverdOrders)
			.completedOrders(completedOrders)
			.cancelledOrders(cancelledOrders)
			.allOrders(allOrders)
			.build();
	}


	@Override
	//查询菜品总览
	public DishOverViewVO getDishOverView(){

		Map map = new HashMap();
		map.put("status" , StatusConstant.ENABLE);
		Integer sold = dishMapper.countByMap(map);
		map.put("status" , StatusConstant.DISABLE);
		Integer discontinued = dishMapper.countByMap(map);
		return DishOverViewVO.builder()
			.sold(sold)
			.discontinued(discontinued)
			.build();
	}

	//查询套餐总览
	@Override
	public SetmealOverViewVO getSetmealOverView(){
		Map map = new HashMap();
		map.put("status" , StatusConstant.ENABLE);
		Integer sold = setmealMapper.countByMap(map);
		map.put("status" , StatusConstant.DISABLE);
		Integer discontinued = setmealMapper.countByMap(map);
		return SetmealOverViewVO.builder()
			.sold(sold)
			.discontinued(discontinued)
			.build();


	}






}
