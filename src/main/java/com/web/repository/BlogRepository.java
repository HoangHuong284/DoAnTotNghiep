package com.web.repository;

import com.web.entity.Blog;
import com.web.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog,Long> {

    @Modifying
    @Transactional
//    @Query("update Blog b set b.primaryBlog = false")
//    default int unSetPrimary() {
//        int i = 0;
//        return i;
//    }

    @Query(value = "select * from blog b order by b.id desc limit 1", nativeQuery = true)
    public Optional<Blog> newBlog();
}
