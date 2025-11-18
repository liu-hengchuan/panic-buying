package com.example.myspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myspringboot.entity.ClassicBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 经典典籍Mapper
 */
@Mapper
public interface ClassicBookMapper extends BaseMapper<ClassicBook> {
}
