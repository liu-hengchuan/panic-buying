package com.example.myspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SoldUserMapper {
    int insert(@Param("goodsId") String goodsId, @Param("userId") String userId);
    int delete(@Param("goodsId") String goodsId, @Param("userId") String userId);
    int countByGoodsIdAndUserId(@Param("goodsId") String goodsId, @Param("userId") String userId);
}
