package com.web.repository;

import com.web.entity.Category;
import com.web.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {

    @Query("select p from Product p where p.id = ?1 and p.deleted <> true")
    public Optional<Product> findById(Long id);

    @Query("select p from Product p where p.deleted <> true ")
    public Page<Product> findAll(Pageable pageable);

    @Query("select p from Product p where p.deleted <> true ")
    public List<Product> findAll();

    @Query(value = "SELECT d.*, MATCH(d.name) AGAINST(?1) as score from product d where MATCH(d.name) AGAINST (?1) > 0 and d.deleted != true order by score desc", nativeQuery = true)
    Page<Product> searchFull(String search, Pageable pageable);

    @Query("select p from Product p where p.category.id = ?1 and p.deleted <> true")
    List<Product> findByCategory(Long categoryId);

}
