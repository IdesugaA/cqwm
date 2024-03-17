package com.own.service;


import com.own.vo.BusinessDataVO;
import com.own.vo.DishOverViewVO;
import com.own.vo.OrderOverViewVO;
import com.own.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkspaceService{

	//根据时间段统计营业数据
	BusinessDataVO getBusinessData(LocalDateTime begin , LocalDateTime end);


	//查询订单管理数据
	OrderOverViewVO gerOrderOverView();

	//查询菜品总览
	DishOverViewVO getDishOverView();

	//查询套餐总览
	SetmealOverViewVO getSetmealOverView();



}
