package com.example.myspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myspringboot.entity.TongueDiagnosis;
import com.example.myspringboot.service.TongueDiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 舌诊控制器
 */
@RestController
@RequestMapping("/api/tongue-diagnosis")
public class TongueDiagnosisController {
    @Autowired
    private TongueDiagnosisService tongueDiagnosisService;

    /**
     * 获取所有舌诊记录
     * @return 舌诊列表
     */
    @GetMapping
    public List<TongueDiagnosis> getAllTongueDiagnosis() {
        return tongueDiagnosisService.list();
    }

    /**
     * 分页查询舌诊记录
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<TongueDiagnosis> getTongueDiagnosisPage(@RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "10") Integer size) {
        Page<TongueDiagnosis> tonguePage = new Page<>(page, size);
        return tongueDiagnosisService.page(tonguePage);
    }

    /**
     * 根据ID获取舌诊记录
     * @param id 舌诊ID
     * @return 舌诊记录
     */
    @GetMapping("/{id}")
    public TongueDiagnosis getTongueDiagnosisById(@PathVariable Integer id) {
        return tongueDiagnosisService.getById(id);
    }

    /**
     * 新增舌诊记录
     * @param tongueDiagnosis 舌诊记录
     * @return 是否成功
     */
    @PostMapping
    public boolean addTongueDiagnosis(@RequestBody TongueDiagnosis tongueDiagnosis) {
        return tongueDiagnosisService.save(tongueDiagnosis);
    }

    /**
     * 更新舌诊记录
     * @param tongueDiagnosis 舌诊记录
     * @return 是否成功
     */
    @PutMapping
    public boolean updateTongueDiagnosis(@RequestBody TongueDiagnosis tongueDiagnosis) {
        return tongueDiagnosisService.updateById(tongueDiagnosis);
    }

    /**
     * 删除舌诊记录
     * @param id 舌诊ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean deleteTongueDiagnosis(@PathVariable Integer id) {
        return tongueDiagnosisService.removeById(id);
    }
}
