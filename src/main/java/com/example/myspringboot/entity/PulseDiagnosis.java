package com.example.myspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 脉诊实体类
 */
@Data
@TableName("pulse_diagnosis")
public class PulseDiagnosis {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String pinyin;
    private String description;
    private String mechanism;
    private String imageUrl;
    private String syndromes;
    private String prescriptions;
}
