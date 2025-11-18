package com.example.myspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myspringboot.entity.Prescription;
import com.example.myspringboot.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 方剂控制器
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {
    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * 获取所有方剂
     * @return 方剂列表
     */
    @GetMapping
    public List<Prescription> getAllPrescriptions() {
        return prescriptionService.list();
    }

    /**
     * 分页查询方剂
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<Prescription> getPrescriptionsPage(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Page<Prescription> prescriptionPage = new Page<>(page, size);
        return prescriptionService.page(prescriptionPage);
    }

    /**
     * 根据ID获取方剂
     * @param id 方剂ID
     * @return 方剂
     */
    @GetMapping("/{id}")
    public Prescription getPrescriptionById(@PathVariable Integer id) {
        return prescriptionService.getById(id);
    }

    /**
     * 根据名称或拼音搜索方剂
     * @param keyword 关键词
     * @return 方剂列表
     */
    @GetMapping("/search")
    public List<Prescription> searchPrescriptions(@RequestParam String keyword) {
        QueryWrapper<Prescription> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword).or().like("pinyin", keyword);
        return prescriptionService.list(wrapper);
    }

    /**
     * 根据分类获取方剂
     * @param category 分类
     * @return 方剂列表
     */
    @GetMapping("/category/{category}")
    public List<Prescription> getPrescriptionsByCategory(@PathVariable String category) {
        QueryWrapper<Prescription> wrapper = new QueryWrapper<>();
        wrapper.eq("category", category);
        return prescriptionService.list(wrapper);
    }

    /**
     * 新增方剂
     * @param prescription 方剂
     * @return 是否成功
     */
    @PostMapping
    public boolean addPrescription(@RequestBody Prescription prescription) {
        return prescriptionService.save(prescription);
    }

    /**
     * 更新方剂
     * @param prescription 方剂
     * @return 是否成功
     */
    @PutMapping
    public boolean updatePrescription(@RequestBody Prescription prescription) {
        return prescriptionService.updateById(prescription);
    }

    /**
     * 删除方剂
     * @param id 方剂ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean deletePrescription(@PathVariable Integer id) {
        return prescriptionService.removeById(id);
    }
}
