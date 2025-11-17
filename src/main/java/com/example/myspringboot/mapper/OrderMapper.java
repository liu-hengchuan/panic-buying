package com.example.myspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myspringboot.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    List<Order> selectByUserId(@Param("userId") String userId);
    List<Order> selectByGoodsId(@Param("goodsId") String goodsId);
}
