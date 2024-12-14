package com.web.api;

import com.web.dto.CommentRequest;
import com.web.dto.ProductCommentResponse;
import com.web.entity.ProductComment;
import com.web.service.ProductCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/product-comment")
@CrossOrigin
public class ProductCommentApi {

    @Autowired
    private ProductCommentService productCommentService;

    @PostMapping("/user/create")
    public ResponseEntity<?> save(@RequestBody CommentRequest commentRequest){
        ProductComment result = productCommentService.create(commentRequest);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/all/delete")
    public ResponseEntity<?> delete(@RequestParam("id") Long id){
        productCommentService.delete(id);
        return new ResponseEntity<>("success",HttpStatus.OK);
    }

    @GetMapping("/public/find-by-product")
    public ResponseEntity<?> findAll(@RequestParam("idproduct") Long idproduct){
        List<ProductComment> result = productCommentService.findByProductId(idproduct);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
