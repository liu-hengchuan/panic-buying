package com.example.myspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 中药实体类
 */
@Data
@TableName("chinese_medicine")
public class ChineseMedicine {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String pinyin;
    private String latin;
    private String property;
    private String channel;
    private String function;
    private String indication;
    private String usage;
    private String caution;
    private String imageUrl;
}
