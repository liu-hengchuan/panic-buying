package com.example.myspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myspringboot.entity.Prescription;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方剂Mapper
 */
@Mapper
public interface PrescriptionMapper extends BaseMapper<Prescription> {
}
