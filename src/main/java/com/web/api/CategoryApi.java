package com.web.api;

import com.web.entity.Category;
import com.web.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class CategoryApi {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/public/all-category")
    public ResponseEntity<?> findAllCategory() {
        List<Category> result = categoryRepository.findAll();
        return new ResponseEntity(result, HttpStatus.CREATED);
    }


    @PostMapping("/admin/create")
    public ResponseEntity<?> save(@RequestBody Category category){
        Category result = categoryRepository.save(category);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        categoryRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin/findById")
    public ResponseEntity<?> findById(@RequestParam("id") Long id){
        Category result = categoryRepository.findById(id).get();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
