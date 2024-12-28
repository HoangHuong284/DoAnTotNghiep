package com.web.service;

import com.web.entity.ImportProduct;
import com.web.entity.Product;
import com.web.exception.MessageException;
import com.web.repository.ImportProductRepository;
import com.web.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Component
public class ImportProductService {

    @Autowired
    private ImportProductRepository importProductRepository;

    @Autowired
    private ProductRepository productRepository;

    
    public ImportProduct create(ImportProduct importProduct) {
        if(importProduct.getId() != null){
            throw new MessageException("id must null");
        }
        Optional<Product> product = productRepository.findById(importProduct.getProduct().getId());
        if(product.isEmpty()){
            throw new MessageException("product not found");
        }
        if (importProduct.getQuantity() <= 0) {
            throw new MessageException("Quantity must be greater than 0");
        }
        importProduct.setImportDate(new Date(System.currentTimeMillis()));
        importProduct.setImportTime(new Time(System.currentTimeMillis()));
        ImportProduct result = importProductRepository.save(importProduct);
        product.get().setQuantity(result.getQuantity() + product.get().getQuantity());
        productRepository.save(product.get());
        return result;
    }

    
    public ImportProduct update(ImportProduct importProduct) {
        if(importProduct.getId() == null){
            throw new MessageException("id require");
        }
        if (importProduct.getQuantity() <= 0) {
            throw new MessageException("Quantity must be greater than 0");
        }
        Optional<ImportProduct> exist = importProductRepository.findById(importProduct.getId());
        if(exist.isEmpty()){
            throw new MessageException("not found");
        }

        Product product = exist.get().getProduct();
        product.setQuantity(product.getQuantity() - exist.get().getQuantity());
        if(product.getId() != importProduct.getProduct().getId()){
            if (product.getQuantity() < 0){
                throw new MessageException("Số lượng sản phẩm "+product.getName()+" < 0");
            }
        }

        Optional<Product> sz = productRepository.findById(importProduct.getProduct().getId());
        sz.get().setQuantity(sz.get().getQuantity() + importProduct.getQuantity());
        if (sz.get().getQuantity() < 0){
            throw new MessageException("Số lượng sản phẩm"+product.getName()+" < 0");
        }

        productRepository.save(product);
        productRepository.save(sz.get());

        importProduct.setImportDate(exist.get().getImportDate());
        importProduct.setImportTime(exist.get().getImportTime());
        ImportProduct result = importProductRepository.save(importProduct);
        return result;
    }

    
    public void delete(Long id) {
        Optional<ImportProduct> exist = importProductRepository.findById(id);
        if(exist.isEmpty()){
            throw new MessageException("not found");
        }
        Product product = exist.get().getProduct();;
        product.setQuantity(product.getQuantity() - exist.get().getQuantity());
        if(product.getQuantity() < 0){
            throw new MessageException("Quantity of product < 0 ");
        }
        productRepository.save(product);
        importProductRepository.delete(exist.get());
    }

    
    public ImportProduct findById(Long id) { Optional<ImportProduct> exist = importProductRepository.findById(id);
        if(exist.isEmpty()){
            throw new MessageException("not found");
        }
        return exist.get();
    }

    
    public Page<ImportProduct> getAll(Pageable pageable) {
        Page<ImportProduct> page = importProductRepository.findAll(pageable);
        return page;
    }

    
    public List<ImportProduct> getByProductAndDate(Long productId, Date from, Date to) {
        if(from == null || to == null){
            from = Date.valueOf("2000-01-01");
            to = Date.valueOf("2200-01-01");
        }
        System.out.println("==== from: "+from);
        System.out.println("==== to: "+to);
        List<ImportProduct> importProducts = null;
        if(productId == null){
            importProducts = importProductRepository.findByDate(from,to);
        }
        else{
            importProducts = importProductRepository.findByDateAndProduct(from,to,productId);
        }
        return importProducts;
    }
}
