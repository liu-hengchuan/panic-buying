package com.example.myspringboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myspringboot.entity.ClassicBook;
import com.example.myspringboot.mapper.ClassicBookMapper;
import com.example.myspringboot.service.ClassicBookService;
import org.springframework.stereotype.Service;

/**
 * 经典典籍Service实现类
 */
@Service
public class ClassicBookServiceImpl extends ServiceImpl<ClassicBookMapper, ClassicBook> implements ClassicBookService {
}
