package com.web.api;

import com.web.dto.ProductExcel;
import com.web.dto.ProductSearch;
import com.web.entity.Product;
import com.web.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@CrossOrigin
public class ProductApi {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/create")
    public ResponseEntity<?> save(@RequestBody Product product){
        Product response = productService.save(product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/admin/update")
    public ResponseEntity<?> update(@RequestBody Product product){
        Product response = productService.update(product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        productService.delete(id);
        return new ResponseEntity<>("delete success", HttpStatus.OK);
    }

    @GetMapping("/public/findById")
    public ResponseEntity<?> findByIdForAdmin(@RequestParam("id") Long id){
        Product response = productService.findById(id);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/public/findAll-list")
    public ResponseEntity<?> findAllList(){
        List<Product> response = productService.findAllList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/public/findAll-page")
    public ResponseEntity<?> findAllPage(Pageable pageable){
        Page<Product> result = productService.findAllPage(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/public/search-full")
    public Page<Product> getProductsByCriteria(@RequestBody ProductSearch search, Pageable pageable) {
        return productService.findProductsByCriteria(search.getCategoryIds(), search.getMinPrice(), search.getMaxPrice(), pageable);
    }

    @GetMapping("/public/search-by-param")
    public Page<Product> getProductsByCriteria(@RequestParam String search, Pageable pageable) {
        return productService.searchProduct(search, pageable);
    }

    @GetMapping("/admin/export-excel")
    public ResponseEntity<?> exportExel(){
        List<ProductExcel> result = productService.excelProduct();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
