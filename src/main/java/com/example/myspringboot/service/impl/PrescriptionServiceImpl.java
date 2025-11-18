package com.example.myspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myspringboot.entity.Prescription;
import com.example.myspringboot.mapper.PrescriptionMapper;
import com.example.myspringboot.service.PrescriptionService;
import org.springframework.stereotype.Service;

/**
 * 方剂Service实现类
 */
@Service
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionMapper, Prescription> implements PrescriptionService {
}
