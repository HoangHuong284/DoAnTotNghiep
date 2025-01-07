package com.web.api;

import com.web.dto.PaymentDto;
import com.web.dto.ResponsePayment;
import com.web.entity.Cart;
import com.web.entity.Voucher;
import com.web.exception.MessageException;
import com.web.repository.CartRepository;
import com.web.service.VoucherService;
import com.web.utils.UserUtils;
import com.web.vnpay.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vnpay")
@CrossOrigin
public class VnpayApi {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VNPayService vnPayService;

// goi api nay de tao url cho ng thanh taon
    @PostMapping("/urlpayment")
    public ResponsePayment getUrlPayment(@RequestBody PaymentDto paymentDto){
        Double totalAmount = 0D;
        List<Cart> carts = cartRepository.findByUser(userUtils.getUserWithAuthority().getId());

        for(Cart p : carts){
            if(p.getProduct().getQuantity() < p.getQuantity()){
                throw new MessageException("Số lượng sản phẩm "+p.getProduct().getName()+" chỉ còn "+p.getQuantity());
            }
            if(p.getProduct().getQuantity() == 0 ){
                throw new MessageException("Số lượng sản phẩm "+p.getProduct().getName()+" đã hết ");
            }
            if(p.getQuantity() == 0 ){
                throw new MessageException("Số lượng sản phẩm "+p.getProduct().getName()+" không được < 1");
            }

            totalAmount += p.getProduct().getPrice() * p.getQuantity();
        }
        if(paymentDto.getCodeVoucher() != null){
            Optional<Voucher> voucher = voucherService.findByCode(paymentDto.getCodeVoucher(), totalAmount);
            if(voucher.isPresent()){
                totalAmount = totalAmount - voucher.get().getDiscount();
            }
        }
        String orderId = String.valueOf(System.currentTimeMillis());
        String vnpayUrl = vnPayService.createOrder(totalAmount.intValue(), orderId, paymentDto.getReturnUrl());
        ResponsePayment responsePayment = new ResponsePayment(vnpayUrl,orderId,null);
        return responsePayment;
    }
}
