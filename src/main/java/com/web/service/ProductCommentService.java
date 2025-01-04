package com.web.service;

import com.web.dto.CommentRequest;
import com.web.entity.Invoice;
import com.web.entity.InvoiceDetail;
import com.web.entity.ProductComment;
import com.web.entity.User;
import com.web.enums.Status;
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
        List<InvoiceDetail> invoiceDetails = invoiceDetailRepository.findByUserAndProductId(
                userUtils.getUserWithAuthority().getId(),
                commentRequest.getProduct().getId()
        );

        if( invoiceDetails.isEmpty()) {
            throw new MessageException("Bạn chưa mua sản phẩm này, không thể bình luận sản phẩm !");
        }

        String content = commentRequest.getContent();
        if (content == null || content.trim().isEmpty()) {
            throw new MessageException("Nội dung bình luận không được để trống!");
        }

        boolean hasReceiveOrder = invoiceDetails.stream().anyMatch(
                invoiceDetail -> {
                    Invoice invoice = invoiceDetail.getInvoice();
                    return invoice.getStatus() == Status.DA_NHAN;
                }
        );

        if(!hasReceiveOrder){
            throw new MessageException("Bạn cần nhận hàng trước khi có thể bình luận sản phẩm");
        }

        ProductComment productComment = new ProductComment();
        productComment.setProduct(commentRequest.getProduct());
        productComment.setContent(commentRequest.getContent());
        productComment.setStar(commentRequest.getStar());
        productComment.setCreatedDate(new Date(System.currentTimeMillis()));
        productComment.setCreatedTime(new Time(System.currentTimeMillis()));
        productComment.setUser(userUtils.getUserWithAuthority());

        return productCommentRepository.save(productComment);


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
        if(optional.get().getUser().getId() != userUtils.getUserWithAuthority().getId()){
            throw new MessageException("Access denied");
        }

        if(user.getAuthorities().getName().equals(Contains.ROLE_ADMIN) || user.getAuthorities().getName().equals(Contains.ROLE_USER)){
            productCommentRepository.deleteById(id);
            return;
        }

        productCommentRepository.deleteById(id);
    }

    

    public List<ProductComment> findByProductId(Long productId) {
        List<ProductComment> list = productCommentRepository.findByProductId(productId);
        User user = userUtils.getUserWithAuthority();
        if(user != null){
            for(ProductComment p : list){
                if(p.getUser().getId().equals(user.getId())){
                    p.setIsMyComment(true);
                }else {
                    p.setIsMyComment(false);
                }

            }
        }
        return list;
    }
}
