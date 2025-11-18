package com.example.myspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 方剂实体类
 */
@Data
@TableName("prescription")
public class Prescription {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String pinyin;
    private String source;
    private String category;
    private String composition;
    private String dosage;
    private String function;
    private String indication;
    private String explanation;
    private String syndromes;
}
