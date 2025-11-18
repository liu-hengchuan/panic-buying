package com.example.myspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myspringboot.entity.PulseDiagnosis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 脉诊Mapper
 */
@Mapper
public interface PulseDiagnosisMapper extends BaseMapper<PulseDiagnosis> {
}
