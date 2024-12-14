package com.web.service;

import com.web.dto.CommentRequest;
import com.web.entity.ProductComment;
import com.web.entity.User;
import com.web.exception.MessageException;
import com.web.repository.InvoiceDetailRepository;
import com.web.repository.ProductCommentRepository;
import com.web.utils.Contains;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Optional;

@Component
public class ProductCommentService {

    @Autowired
    private ProductCommentRepository productCommentRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    
    public ProductComment create(CommentRequest commentRequest) {
//        if(invoiceDetailRepository.findByUserAndProductId(userUtils.getUserWithAuthority().getId(), commentRequest.getProduct().getId()).size() == 0){
//            throw new MessageException("Bạn chưa mua sản phẩm này, không thể bình luận sản phẩm");
//        }
        ProductComment productComment = new ProductComment();
        productComment.setContent(commentRequest.getContent());
        productComment.setProduct(commentRequest.getProduct());
        productComment.setStar(commentRequest.getStar());
        productComment.setCreatedDate(new Date(System.currentTimeMillis()));
        productComment.setCreatedTime(new Time(System.currentTimeMillis()));
        productComment.setUser(userUtils.getUserWithAuthority());
        ProductComment result = productCommentRepository.save(productComment);
        return result;
    }

    
    public void delete(Long id) {
        if(id == null){
            throw new MessageException("id require");
        }
        Optional<ProductComment> optional = productCommentRepository.findById(id);
        if(optional.isEmpty()){
            throw new MessageException("comment not found");
        }
        User user = userUtils.getUserWithAuthority();
        if(user.getAuthorities().getName().equals(Contains.ROLE_ADMIN) || user.getAuthorities().getName().equals(Contains.ROLE_USER)){
            productCommentRepository.deleteById(id);
            return;
        }
        if(optional.get().getUser().getId() != userUtils.getUserWithAuthority().getId()){
            throw new MessageException("access denied");
        }
        productCommentRepository.deleteById(id);
    }

    

    public List<ProductComment> findByProductId(Long productId) {
        List<ProductComment> list = productCommentRepository.findByProductId(productId);
        User user = userUtils.getUserWithAuthority();
        if(user != null){
            for(ProductComment p : list){
                if(p.getUser().getId() == user.getId()){
                    p.setIsMyComment(true);
                }
                if(user.getAuthorities().getName().equals(Contains.ROLE_ADMIN) || user.getAuthorities().getName().equals(Contains.ROLE_USER)){
                    p.setIsMyComment(true);
                }
            }
        }
        return list;
    }
}
