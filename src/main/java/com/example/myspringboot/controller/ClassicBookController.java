package com.example.myspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.myspringboot.entity.ClassicBook;
import com.example.myspringboot.service.ClassicBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 经典典籍控制器
 */
@RestController
@RequestMapping("/api/classic-books")
public class ClassicBookController {
    @Autowired
    private ClassicBookService classicBookService;

    /**
     * 获取所有经典典籍
     * @return 经典典籍列表
     */
    @GetMapping
    public List<ClassicBook> getAllClassicBooks() {
        return classicBookService.list();
    }

    /**
     * 分页查询经典典籍
     * @param page 页码
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/page")
    public Page<ClassicBook> getClassicBooksPage(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        Page<ClassicBook> classicBookPage = new Page<>(page, size);
        return classicBookService.page(classicBookPage);
    }

    /**
     * 根据ID获取经典典籍
     * @param id 典籍ID
     * @return 经典典籍
     */
    @GetMapping("/{id}")
    public ClassicBook getClassicBookById(@PathVariable Integer id) {
        return classicBookService.getById(id);
    }

    /**
     * 根据分类获取经典典籍
     * @param category 分类
     * @return 经典典籍列表
     */
    @GetMapping("/category/{category}")
    public List<ClassicBook> getClassicBooksByCategory(@PathVariable String category) {
        QueryWrapper<ClassicBook> wrapper = new QueryWrapper<>();
        wrapper.eq("category", category);
        return classicBookService.list(wrapper);
    }

    /**
     * 新增经典典籍
     * @param classicBook 经典典籍
     * @return 是否成功
     */
    @PostMapping
    public boolean addClassicBook(@RequestBody ClassicBook classicBook) {
        return classicBookService.save(classicBook);
    }

    /**
     * 更新经典典籍
     * @param classicBook 经典典籍
     * @return 是否成功
     */
    @PutMapping
    public boolean updateClassicBook(@RequestBody ClassicBook classicBook) {
        return classicBookService.updateById(classicBook);
    }

    /**
     * 删除经典典籍
     * @param id 典籍ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean deleteClassicBook(@PathVariable Integer id) {
        return classicBookService.removeById(id);
    }
}
