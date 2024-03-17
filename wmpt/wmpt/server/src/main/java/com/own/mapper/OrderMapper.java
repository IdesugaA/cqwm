package com.own.mapper;


import com.own.dto.GoodsSalesDTO;
import com.own.dto.OrdersPageQueryDTO;
import com.own.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper{

	void insert(Orders orders);

	@Select("select * from orders where number = #{orderNumber}")
	Orders getByNumberAndUserId(String orderNumber);

	void update(Orders orders);

	@Select("select * from orders where status = #{status} and order_time <= #{compareTime}")
	List<Orders> getByStatusAndOrdertimeLT(Integer status , LocalDateTime compareTime);

	/**
	*	status设置取消状态值
	*	msg取消原因
	*	cancelTime取消时间
	*	ids设置状态的id列表集合
	*/
	void updateStatus(Integer status , String msg , LocalDateTime cancelTime , List<Long> ids);

	/**
	 *	status 设置状态值
	 *	deliveryTime 取消时间
	 *	ids 设置状态的id列表集合
	 */
	void updateDeliveryStatus(Integer status , LocalDateTime deliveryTime , List<Long> ids);

	//根据条件查询订单
	List<Orders> findByCondition(OrdersPageQueryDTO ordersPageQueryDTO);

	Orders findById(Long id);

	//根据条件查询订单总金额
	Double sumByMap(Map map);


	//根据条件获取订单总数
	Integer countByMap(Map map);


	List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime , LocalDateTime endTime);

}
