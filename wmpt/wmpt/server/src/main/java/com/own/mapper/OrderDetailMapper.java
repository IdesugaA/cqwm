package com.own.mapper;


import com.own.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    //批量插入订单详情
    void insertBatch(List<OrderDetail> orderDetailList);

    //根据订单主表id查询订单详细表列表数据
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> findByOrderId(Long orderId);

}
