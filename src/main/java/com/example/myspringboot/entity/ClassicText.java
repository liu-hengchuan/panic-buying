package com.example.myspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 经典条文实体类
 */
@Data
@TableName("classic_text")
public class ClassicText {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Integer bookId;
    private String chapter;
    private String section;
    private String content;
    private String translation;
    private String interpretation;
    private String tags;
}
