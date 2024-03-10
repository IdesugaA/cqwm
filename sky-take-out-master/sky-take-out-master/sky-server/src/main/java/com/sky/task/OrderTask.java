package com.sky.task;

import com.sky.constant.MessageConstant;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description OrderTask
 * @Author songyu
 * @Date 2023-10-06
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OrderTask {

    private final OrderMapper orderMapper;

    //目标：每分钟执行一次超时支付订单状态检查
    //@Scheduled(cron = "0 * * * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void processPayTimeoutOrder(){

        log.info("开始执行超时订单处理...");
        //1.查找订单状态为“待付款”和下单时间超时15分钟的订单列表List<Orders>
        //sql分析： select * from orders where status = 1 and order_time <= (当前时间-15分钟)
        //计算当前时间-15分钟
        LocalDateTime compareTime = LocalDateTime.now().minusMinutes(15);
        //查询指定的时间
        List<Orders> ordersList =  orderMapper.getByStatusAndOrdertimeLT(Orders.PENDING_PAYMENT,compareTime);
        if(!CollectionUtils.isEmpty(ordersList)) {
            //将List<Orders>转换为List<Long>的id列表集合
            List<Long> ids = ordersList.stream().map(Orders::getId).collect(Collectors.toList());

            //2.修改这些订单的状态为已取消
            //sql分析： update orders set status = 6,cancel_reason='超时未支付',cancel_time=#{time} where id in (id1,id2,...)
            orderMapper.updateStatus(Orders.CANCELLED, MessageConstant.ORDER_TIMEOUT_UNPAY, LocalDateTime.now(), ids);
            log.info("执行超时订单处理结束...成功处理了{}笔订单！",ids.size());
        }else{
            log.info("执行超时订单处理结束...没有需要处理的订单！");
        }
    }


    //目标：每天凌晨1点处理派送中的订单修改为已完成
    //@Scheduled(cron = "0 0 1 * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void processDeliveryOrder(){

        log.info("开始执行派送中订单为已完成处理...");
        //1.查找订单状态为“派送中”和下单时间超时60分钟的订单列表List<Orders>
        //sql分析： select * from orders where status = 4 and order_time <= (当前时间-60分钟)
        //计算当前时间-60分钟
        LocalDateTime compareTime = LocalDateTime.now().minusMinutes(60);
        //查询指定的时间和派送中的订单列表
        List<Orders> ordersList =  orderMapper.getByStatusAndOrdertimeLT(Orders.DELIVERY_IN_PROGRESS,compareTime);
        if(!CollectionUtils.isEmpty(ordersList)) {
            //将List<Orders>转换为List<Long>的id列表集合
            List<Long> ids = ordersList.stream().map(Orders::getId).collect(Collectors.toList());

            //2.修改这些订单的状态为已完成
            //sql分析： update orders set status = 5,cancel_reason=null,cancel_time=null where id in (id1,id2,...)
            orderMapper.updateDeliveryStatus(Orders.COMPLETED,  LocalDateTime.now(), ids);
            log.info("执行派送中订单为已完成处理结束...成功处理了{}笔订单！",ids.size());
        }else{
            log.info("执行派送中订单为已完成处理结束...没有需要处理的订单！");
        }
    }

}
