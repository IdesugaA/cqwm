package com.own.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.own.constant.MessageConstant;
import com.own.context.BaseContext;
import com.own.dto.OrdersPageQueryDTO;
import com.own.dto.OrdersPaymentDTO;
import com.own.dto.OrdersSubmitDTO;
import com.own.entity.*;
import com.own.exception.OrderBusinessException;
import com.own.mapper.*;
import com.own.result.PageResult;
import com.own.service.OrderService;
import com.own.utils.HttpClientUtil;
import com.own.utils.WeChatPayUtil;
import com.own.vo.OrderPaymentVO;
import com.own.vo.OrderSubmitVO;
import com.own.vo.OrderVO;
import com.own.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

	private final OrderMapper orderMapper;

	private final AddressBookMapper addressBookMapper;

	private final UserMapper userMapper;

	private final OrderDetailMapper orderDetailMapper;

	private final ShoppingCartMapper shoppingCartMapper;

	@Override
	public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO){

		//准备数据，根据收获地址id获取
		//地址簿对象数据
		AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
		//校验收获地址
		checkOutOfRange(addressBook.getCityName()+
			addressBook.getDistrictName()+addressBook.getDetail());
		Orders orders = new Orders();
		//将前端数据ordersSubmitDTO赋值给orders
		BeanUtils.copyProperties(ordersSubmitDTO,orders);
		//根据登录人id获取登录的用户数据
		User user = userMapper.findById(BaseContext.getCurrentId());
		//补全：订单号，uuid，注意，后面会给到微信，微信订单号要求必须小于等于32字符长度）
		orders.setNumber(UUID.randomUUID().toString().replace("-",""));//得到一个32个长度唯一值
		//补全：订单状态status，默认待付款
		orders.setStatus(Orders.PENDING_PAYMENT);
		//补全：用户id user_id 登录人id
		orders.setUserId(BaseContext.getCurrentId());
		//补全：下单时间 order_time 当前时间
		orders.setOrderTime(LocalDateTime.now());
		//补全：支付状态 pay_status 未支付
		orders.setPayStatus(Orders.UN_PAID);
		//补全：收货人手机号，地址，收货人名字,phone,address,consignee，根据收货地址表获取
		orders.setPhone(addressBook.getPhone());
		orders.setAddress(addressBook.getProvinceName()+addressBook.getCityName()+
		addressBook.getDistrictName()
		+addressBook.getDetail());
		orders.setConsignee(addressBook.getConsignee());
		//补全：登录人名字 user_name，根据登录人数据获取
		orders.setUserName(user.getName());

		//插入订单主表数据
		orderMapper.insert(orders);
		
		//插入订单详情表数据
		//获取登录人的购物车集合数据List
		List<ShoppingCart> shoppingCartList = shoppingCartMapper.findListByUserId(BaseContext.getCurrentId());
		//遍历List，将每个ShoppingCart对象封装成OrderDetail，并且设置逻辑外键order_id，转换为List<OrderDetail>
		List<OrderDetail> orderDetailList = shoppingCartList.stream().map(shoppingCart ->{
		
			OrderDetail orderDetail = new OrderDetail();
			//将每个ShoppingCart对象封装成OrderDetail
			BeanUtils.copyProperties(shoppingCart,orderDetail);
			//设置逻辑外键order_id
			orderDetail.setOrderId(orders.getId());
			return orderDetail;	
		}).collect(Collectors.toList());
		//批量插入List<OrderDetail>
		orderDetailMapper.insertBatch(orderDetailList);
		//清空购物车
		shoppingCartMapper.clean(BaseContext.getCurrentId());
		//封装OrderSubmitVO对象数据返回
		return OrderSubmitVO.builder()
			.id(orders.getId())
			.orderAmount(orders.getAmount())
			.orderTime(orders.getOrderTime())
			.orderNumber(orders.getNumber())
			.build();

	}

	private final WeChatPayUtil weChatPayUtil;

	//订单支付
	
	public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception{

		Long userId = BaseContext.getCurrentId();
		User user = userMapper.findById(userId);
		//调用微信支付接口,生成预支付交易单
		JSONObject jsonObject = weChatPayUtil.pay(
				ordersPaymentDTO.getOrderNumber(),//商户订单号
				new BigDecimal(0.01),//支付金额，单元远
				"苍穹外卖订单",//商品描述
				user.getOpenid() //微信用户的openid
		);
		if(jsonObject.getString("code")!=null && jsonObject.getString("code").equals("ORDERPAID")){
			throw new OrderBusinessException("该订单已支付");
		}
		OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
		vo.setPackageStr(jsonObject.getString("package"));
		return vo;

	}
	
	private final WebSocketServer webSocketServer;

	public void paySuccess(String outTradeNo){

		//根据订单号查询当前用户的订单
		Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo);

		//根据订单id更新订单的状态，支付方式，支付状态，结账时间
		Orders orders = Orders.builder()
			.id(ordersDB.getId())
			.status(Orders.TO_BE_CONFIRMED)
			.payStatus(Orders.PAID)
			.checkoutTime(LocalDateTime.now())
			.build();
		orderMapper.update(orders);
		//发送来单提醒到商家端前端
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type",1);
		jsonObject.put("orderId",ordersDB.getId());
		jsonObject.put("content","订单号："+ordersDB.getNumber());
		webSocketServer.sendToAllClient(jsonObject.toJSONString());


	}


	//分页查询订单列表数据
	@Override
	public PageResult page(OrdersPageQueryDTO ordersPageQueryDTO){

		//根据订单主表分页查询数据，得到List<Orders>当前页数据列表
		//sql分析：select * from orders where status = xx order by order_time desc
		//设置分页参数
		PageHelper.startPage(ordersPageQueryDTO.getPage(),
		ordersPageQueryDTO.getPageSize());
		//执行查询
		List<Orders> ordersList = orderMapper.findByCondition(ordersPageQueryDTO);//orderList就是当前页数据列表
		Page<Orders> page = (Page<Orders>) ordersList;
		if(CollectionUtils.isEmpty(ordersList)){
			return null;
		}

		//List<Orders>转换为List<OrderVO>封装订单主表数据和每个订单的订单详情列表数据
		List<OrderVO> orderVOList = ordersList.stream().map(orders ->{

			OrderVO orderVO = new OrderVO();
			BeanUtils.copyProperties(orders,orderVO);
			//根据订单主表id查询订单详情表集合List<OrderDetail>
			List<OrderDetail> orderDetails = orderDetailMapper.findByOrderId(orders.getId());
			//封装给OrderVO
			orderVO.setOrderDetailList(orderDetails);
			return orderVO;
		}).collect(Collectors.toList());

		return PageResult.builder()
			.total(page.getTotal())
			.records(orderVOList)
			.build();

	}

	//根据id查询订单
	@Override
	public Orders findById(Long id){
		return orderMapper.findById(id);
	}

	//查询订单详情
	public OrderVO details(Long id){

		Orders orders = orderMapper.findById(id);
		//查询该订单对应的菜品/套餐明细
		List<OrderDetail> orderDetailList = orderDetailMapper.findByOrderId(orders.getId());

		//将该订单及其详情封装到OrderVO并返回
		OrderVO orderVO = new OrderVO();
		BeanUtils.copyProperties(orders,orderVO);
		orderVO.setOrderDetailList(orderDetailList);
		return orderVO;
	}

	//用户取消订单
	public void userCancelById(Long id)throws Exception{
		Orders ordersDB = orderMapper.findById(id);
		//检验订单是否存在
		if(ordersDB == null){
			throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
		}

		//订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
		if(ordersDB.getStatus() > 2){
			throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
		}

		Orders orders = new Orders();
		orders.setId(ordersDB.getId());

		//订单处于待接单状态下取消，需要进行退款
		if(ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)){
			//调用微信退款接口
			weChatPayUtil.refund(
				ordersDB.getNumber(),//商户订单号
				ordersDB.getNumber(),//商户退款单号
 				new BigDecimal(0.01),//退款金额，单位 元
				new BigDecimal(0.01)); //原订单金额
			//支付状态修改为退款
			orders.setPayStatus(Orders.REFUND);
		}
		//更新订单状态，取消原因，取消时间
		orders.setStatus(Orders.CANCELLED);
		orders.setCancelReason("用户取消");
		orders.setCancelTime(LocalDateTime.now());
		orderMapper.update(orders);

	}

	//再来一单
	public void repetition(Long id){

		Long userId = BaseContext.getCurrentId();
		//根据订单ID查询当前订单详情
		List<OrderDetail> orderDetailList = orderDetailMapper.findByOrderId(id);
		//将订单详情对象转换为购物车对象
		List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x ->{
			ShoppingCart shoppingCart = new ShoppingCart();

			//将原订单详情里面的菜品信息重新复制到购物车对象中
			BeanUtils.copyProperties(x,shoppingCart,"id");
			shoppingCart.setUserId(userId);
			shoppingCart.setCreateTime(LocalDateTime.now());
			return shoppingCart;
		}).collect(Collectors.toList());

		shoppingCartMapper.insertBatch(shoppingCartList);
	}

	private String shopAddress;

	private String ak;

	//检查客户的收货地址是否超出配送范围
	private void checkOutOfRange(String address){
		Map map = new HashMap();
		map.put("address" , shopAddress);
		map.put("output" , "json");
		map.put("ak" , ak);

		//获取店铺的经纬度坐标
		String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3",map);

		JSONObject jsonObject = JSON.parseObject(shopCoordinate);
		if(!jsonObject.getString("status").equals("0")){
			throw new OrderBusinessException("店铺地址解析失败");
		}

		//数据解析
		JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
		String lat = location.getString("lat");
		String lng = location.getString("lng");

		//店铺经纬度坐标
		String shopLngLat = lat + "," + lng;

		map.put("address" , address);

		//获取用户收货地址的经纬度坐标
		String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3",map);
		jsonObject = JSON.parseObject(userCoordinate);
		if(!jsonObject.getString("status").equals("0")){
			throw new OrderBusinessException("收货地址解析失败");
		}


		//数据解析
		location = jsonObject.getJSONObject("result").getJSONObject("location");
		lat = location.getString("lat");
		lng = location.getString("lng");

		//用户收货地址经纬度坐标
		String userLngLat = lat + "," +lng;

		map.put("origin",shopLngLat);
		map.put("destination",userLngLat);
		map.put("steps_info","0");

		//路线规划
		String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving",map);

		jsonObject = JSON.parseObject(json);
		if(!jsonObject.getString("status").equals("0")){
			throw new OrderBusinessException("配送路线规划失败");
		}

		//数据解析
		JSONObject result = jsonObject.getJSONObject("result");
		JSONArray jsonArray = (JSONArray) result.get("route");
		Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

		if(distance > 5000){
			//配送距离超过5000米
			throw new OrderBusinessException("超出配送范围");
		}

	}


}
