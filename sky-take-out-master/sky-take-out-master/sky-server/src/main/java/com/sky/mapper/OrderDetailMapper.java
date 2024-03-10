package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description OrderDetailMapper
 * @Author songyu
 * @Date 2023-10-05
 */
@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单详情
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单主表id查询订单详细表列表数据
     * @param orderId
     * @return
     */
    @Select("select * from  order_detail where order_id = #{orderId}")
    List<OrderDetail> findByOrderId(Long orderId);
}
