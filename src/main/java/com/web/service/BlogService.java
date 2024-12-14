package com.web.service;

import com.web.entity.Blog;
import com.web.exception.MessageException;
import com.web.repository.BlogRepository;
import com.web.utils.CommonPage;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Component
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserUtils userUtils;


    public Blog save(Blog blog) {
        blog.setUser(userUtils.getUserWithAuthority());
        blog.setCreatedDate(new Date(System.currentTimeMillis()));
        Blog result = blogRepository.save(blog);
        return result;
    }

    public void delete(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isEmpty()){
            throw new MessageException("Blog not found");
        }
        if(blog.get().getPrimaryBlog()){
            throw new MessageException("Blog is primary, can't delete");
        }
        blogRepository.delete(blog.get());
    }

    
    public Blog findById(Long id) {
        Optional<Blog> blog = blogRepository.findById(id);
        if (blog.isEmpty()){
            throw new MessageException("Blog not found");
        }
        return blog.get();
    }

    
    public Blog newBlog() {
        Optional<Blog> blog = blogRepository.newBlog();
        if (blog.isEmpty()){
            throw new MessageException("Blog not found");
        }
        return blog.get();
    }

    
    public Page<Blog> findAll(Pageable pageable) {
        Page<Blog> page = blogRepository.findAll(pageable);
        return page;
    }

    public List<Blog> findAllList() {
        List<Blog> page = blogRepository.findAll();
        return page;
    }
}
