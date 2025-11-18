package com.example.myspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myspringboot.entity.ChineseMedicine;
import com.example.myspringboot.mapper.ChineseMedicineMapper;
import com.example.myspringboot.service.ChineseMedicineService;
import org.springframework.stereotype.Service;

/**
 * 中药Service实现类
 */
@Service
public class ChineseMedicineServiceImpl extends ServiceImpl<ChineseMedicineMapper, ChineseMedicine> implements ChineseMedicineService {
}
