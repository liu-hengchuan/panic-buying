package com.example.myspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myspringboot.entity.ChineseMedicine;
import com.example.myspringboot.service.ChineseMedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 中药控制器
 */
@RestController
@RequestMapping("/api/chinese-medicines")
public class ChineseMedicineController {
    @Autowired
    private ChineseMedicineService chineseMedicineService;

    /**
     * 获取所有中药
     * @return 中药列表
     */
    @GetMapping
    public List<ChineseMedicine> getAllChineseMedicines() {
        return chineseMedicineService.list();
    }

    /**
     * 分页查询中药
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<ChineseMedicine> getChineseMedicinesPage(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        Page<ChineseMedicine> medicinePage = new Page<>(page, size);
        return chineseMedicineService.page(medicinePage);
    }

    /**
     * 根据ID获取中药
     * @param id 中药ID
     * @return 中药
     */
    @GetMapping("/{id}")
    public ChineseMedicine getChineseMedicineById(@PathVariable Integer id) {
        return chineseMedicineService.getById(id);
    }

    /**
     * 根据名称或拼音搜索中药
     * @param keyword 关键词
     * @return 中药列表
     */
    @GetMapping("/search")
    public List<ChineseMedicine> searchChineseMedicines(@RequestParam String keyword) {
        QueryWrapper<ChineseMedicine> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword).or().like("pinyin", keyword);
        return chineseMedicineService.list(wrapper);
    }

    /**
     * 新增中药
     * @param medicine 中药
     * @return 是否成功
     */
    @PostMapping
    public boolean addChineseMedicine(@RequestBody ChineseMedicine medicine) {
        return chineseMedicineService.save(medicine);
    }

    /**
     * 更新中药
     * @param medicine 中药
     * @return 是否成功
     */
    @PutMapping
    public boolean updateChineseMedicine(@RequestBody ChineseMedicine medicine) {
        return chineseMedicineService.updateById(medicine);
    }

    /**
     * 删除中药
     * @param id 中药ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean deleteChineseMedicine(@PathVariable Integer id) {
        return chineseMedicineService.removeById(id);
    }
}
