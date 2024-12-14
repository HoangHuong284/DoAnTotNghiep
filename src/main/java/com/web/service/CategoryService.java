package com.web.service;

import com.web.entity.Category;
import com.web.exception.MessageException;
import com.web.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category save(Category category) {
        if(category.getId() != null){
            throw new MessageException("Id must null", 400);
        }
        if(categoryRepository.findByName(category.getName()).isPresent()){
            throw new MessageException("Category name exist");
        }
        Category result = categoryRepository.save(category);
        return result;
    }

    
    public void delete(Long categoryId) {
        if(categoryId == null){
            throw new MessageException("Id require", 400);
        }
        if(categoryRepository.findById(categoryId).isEmpty()){
            throw new MessageException("Not found category :"+categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    
    public Category findById(Long id) {
        return categoryRepository.findById(id).get();
    }

    
    public Page<Category> findAll(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories;
    }


    public List<Category> findAllList() {
        List<Category> list = categoryRepository.findAll();
        return list;
    }
}
