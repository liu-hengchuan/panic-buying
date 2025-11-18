package com.example.myspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myspringboot.entity.ClassicText;
import org.apache.ibatis.annotations.Mapper;

/**
 * 经典条文Mapper
 */
@Mapper
public interface ClassicTextMapper extends BaseMapper<ClassicText> {
}
