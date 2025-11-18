package com.example.myspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myspringboot.entity.PulseDiagnosis;
import com.example.myspringboot.mapper.PulseDiagnosisMapper;
import com.example.myspringboot.service.PulseDiagnosisService;
import org.springframework.stereotype.Service;

/**
 * 脉诊Service实现类
 */
@Service
public class PulseDiagnosisServiceImpl extends ServiceImpl<PulseDiagnosisMapper, PulseDiagnosis> implements PulseDiagnosisService {
}
