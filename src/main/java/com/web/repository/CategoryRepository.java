package com.web.repository;

import com.web.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("select c from Category c where LOWER(c.name) = lower(?1) ")
    Optional<Category> findByName(String namedm);


}
