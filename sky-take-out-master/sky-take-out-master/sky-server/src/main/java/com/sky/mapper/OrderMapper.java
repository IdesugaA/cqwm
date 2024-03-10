package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Description OrderMapper
 * @Author songyu
 * @Date 2023-10-05
 */
@Mapper
public interface OrderMapper {

    void insert(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumberAndUserId(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据指定状态和时间查询订单列表
     * @param status
     * @param compareTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time <= #{compareTime}")
    List<Orders> getByStatusAndOrdertimeLT(Integer status, LocalDateTime compareTime);

    /**
     * 设置订单状态为取消方法
     * @param status 设置取消状态值
     * @param msg 取消原因
     * @param cancelTime  取消时间
     * @param ids 设置状态的id列表集合
     */
    void updateStatus(Integer status, String msg, LocalDateTime cancelTime, List<Long> ids);

    /**
     * 设置订单状态为取消方法
     * @param status 设置状态值
     * @param deliveryTime  取消时间
     * @param ids 设置状态的id列表集合
     */
    void updateDeliveryStatus(Integer status, LocalDateTime deliveryTime, List<Long> ids);

    /**
     * 根据条件查询订单列表数据
     * @param ordersPageQueryDTO
     * @return
     */
    List<Orders> findByCondition(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单数据
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders findById(Long id);

    /**
     * 根据条件查询订单总金额
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据条件获取订单总数
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 获取销量top10数据
     * @param beginTime
     * @param endTime
     * @return
     */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime beginTime, LocalDateTime endTime);
}
