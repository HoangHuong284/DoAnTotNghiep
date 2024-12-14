package com.web.api;

import com.web.entity.Blog;
import com.web.entity.Category;
import com.web.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin
public class BlogApi {

    @Autowired
    private BlogService blogService;

    @PostMapping("/admin/create")
    public ResponseEntity<?> save(@RequestBody Blog blog){
        Blog result = blogService.save(blog);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/employee/create")
    public ResponseEntity<?> saveByEmp(@RequestBody Blog blog){
        Blog result = blogService.save(blog);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        blogService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/employee/delete")
    public ResponseEntity<?> deleteByEmp(@RequestParam("id") Long id){
        blogService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/public/findAll")
    public ResponseEntity<?> findAll(){
        List<Blog> result = blogService.findAllList();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/public/findById")
    public ResponseEntity<?> findById(@RequestParam("id") Long id){
        Blog result = blogService.findById(id);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/public/new-blog")
    public ResponseEntity<?> newBlog(){
        Blog result = blogService.newBlog();
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/public/findAll-page")
    public ResponseEntity<?> findAll(Pageable pageable){
        Page<Blog> result = blogService.findAll(pageable);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
