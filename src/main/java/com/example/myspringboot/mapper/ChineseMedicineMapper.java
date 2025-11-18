package com.example.myspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myspringboot.entity.ChineseMedicine;
import org.apache.ibatis.annotations.Mapper;

/**
 * 中药Mapper
 */
@Mapper
public interface ChineseMedicineMapper extends BaseMapper<ChineseMedicine> {
}
