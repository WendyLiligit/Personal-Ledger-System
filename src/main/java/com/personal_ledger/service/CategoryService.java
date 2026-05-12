package com.personal_ledger.service;

import com.personal_ledger.entity.Category;
import com.personal_ledger.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // 初始化一些预设分类
    @PostConstruct
    public void initCategories() {
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("餐饮", "expense"));
            categoryRepository.save(new Category("交通", "expense"));
            categoryRepository.save(new Category("购物", "expense"));
            categoryRepository.save(new Category("娱乐", "expense"));
            categoryRepository.save(new Category("住房", "expense"));
            categoryRepository.save(new Category("医疗", "expense"));
            categoryRepository.save(new Category("工资", "income"));
            categoryRepository.save(new Category("兼职", "income"));
            categoryRepository.save(new Category("理财", "income"));
        }
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}