package com.example.myspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 经典典籍实体类
 */
@Data
@TableName("classic_book")
public class ClassicBook {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name; // 典籍名称
    private String author; // 作者
    private String dynasty; // 朝代
    private String category; // 分类
    private String intro; // 简介
}
