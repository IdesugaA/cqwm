package com.own.service;


import com.own.dto.OrdersPageQueryDTO;
import com.own.dto.OrdersPaymentDTO;
import com.own.dto.OrdersSubmitDTO;
import com.own.entity.Orders;
import com.own.result.PageResult;
import com.own.vo.OrderPaymentVO;
import com.own.vo.OrderSubmitVO;
import com.own.vo.OrderVO;

public interface OrderService{



	OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


	OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

	void paySuccess(String outTradeNo);

	PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);


	Orders findById(Long id);


	OrderVO details(Long id);

	void userCancelById(Long id) throws Exception;

	void repetition(Long id);



}
