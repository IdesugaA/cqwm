package com.sky.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Description OrderController
 * @Author songyu
 * @Date 2023-10-05
 */
@RestController
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "订单相关接口")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 处理用户下单请求
     * @param ordersSubmitDTO
     * @return
     */
    @ApiOperation("用户下单接口")
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){

        log.info("开始执行用户下单接口:{}",ordersSubmitDTO);

        //调用业务进行用户下单，返回下单VO对象
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);

        //返回数据
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 处理用户历史订单查询请求
     * @return
     */
    @ApiOperation("用户历史订单查询接口")
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){

        log.info("开始执行用户历史订单查询接口：{}",ordersPageQueryDTO);

        //调用业务获取分页数据
        PageResult pageResult = orderService.page(ordersPageQueryDTO);

        //返回数据
        return Result.success(pageResult);
    }

    private final WebSocketServer webSocketServer;


    /**
     * 处理催单请求
     * @param id
     * @return
     */
    @ApiOperation("催单接口")
    @GetMapping("/reminder/{id}")
    public Result reminder(@PathVariable Long id){

        log.info("开始执行催单接口：{}",id);

        //根据订单id查询订单数据
        Orders orders = orderService.findById(id);

        //操作催单，发送数据给商家端
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type",2);
        jsonObject.put("orderId",id);
        jsonObject.put("content","订单号："+orders.getNumber());
        webSocketServer.sendToAllClient(jsonObject.toJSONString());

        //返回数据
        return Result.success();
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> details(@PathVariable("id") Long id) {
        OrderVO orderVO = orderService.details(id);
        return Result.success(orderVO);
    }

    /**
     * 用户取消订单
     *
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable("id") Long id) throws Exception {
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
     * 再来一单
     *
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id) {
        orderService.repetition(id);
        return Result.success();
    }

}
