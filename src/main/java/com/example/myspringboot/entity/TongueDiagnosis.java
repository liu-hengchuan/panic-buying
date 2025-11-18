package com.example.myspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 舌诊实体类
 */
@Data
@TableName("tongue_diagnosis")
public class TongueDiagnosis {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String tongueCoating;
    private String tongueBody;
    private String description;
    private String mechanism;
    private String imageUrl;
    private String syndromes;
    private String prescriptions;
}
