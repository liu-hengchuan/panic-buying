package com.example.myspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myspringboot.entity.TongueDiagnosis;
import com.example.myspringboot.mapper.TongueDiagnosisMapper;
import com.example.myspringboot.service.TongueDiagnosisService;
import org.springframework.stereotype.Service;

/**
 * 舌诊Service实现类
 */
@Service
public class TongueDiagnosisServiceImpl extends ServiceImpl<TongueDiagnosisMapper, TongueDiagnosis> implements TongueDiagnosisService {
}
