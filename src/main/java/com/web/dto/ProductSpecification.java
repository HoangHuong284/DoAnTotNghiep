package com.web.dto;

import com.web.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class ProductSpecification implements Specification<Product> {

    private List<Long> categoryIds;
    private Double minPrice;
    private Double maxPrice;
    private String search;
    public ProductSpecification(String search,List<Long> categoryIds, Double minPrice, Double maxPrice) {
        this.categoryIds = categoryIds;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.search = search;
    }

    @Override
    public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate predicate = cb.conjunction();
        if(search != null && !search.isEmpty()){
            predicate = cb.and(predicate, cb.like(root.get("name"), "%" + search + "%"));
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            predicate = cb.and(predicate, root.get("category").get("id").in(categoryIds));
        }

        if (minPrice != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        predicate = cb.and(predicate, cb.notEqual(root.get("deleted"), true));
        return predicate;
    }
}
