package com.web.api;
import com.web.config.Environment;
import com.web.constants.LogUtils;
import com.web.constants.RequestType;
import com.web.dto.PaymentDto;
import com.web.dto.ResponsePayment;
import com.web.entity.Cart;
import com.web.entity.Voucher;
import com.web.exception.MessageException;
import com.web.models.PaymentResponse;
import com.web.models.QueryStatusTransactionResponse;
import com.web.processor.CreateOrderMoMo;
import com.web.processor.QueryTransactionStatus;
import com.web.repository.CartRepository;
import com.web.service.VoucherService;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class MomoApi {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private VoucherService voucherService;

    @PostMapping("/urlpayment")
    public ResponsePayment getUrlPayment(@RequestBody PaymentDto paymentDto){
        LogUtils.init();
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
        Long td = Math.round(totalAmount);
        System.out.println(totalAmount);
        String orderId = String.valueOf(System.currentTimeMillis());
        String requestId = String.valueOf(System.currentTimeMillis());
        Environment environment = Environment.selectEnv("dev");
        PaymentResponse captureATMMoMoResponse = null;
        try {
            captureATMMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(td), paymentDto.getContent(), paymentDto.getReturnUrl(), paymentDto.getReturnUrl(), "", RequestType.PAY_WITH_ATM, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("url ====: "+captureATMMoMoResponse.getPayUrl());
        ResponsePayment responsePayment = new ResponsePayment(captureATMMoMoResponse.getPayUrl(),orderId,requestId);
        return responsePayment;
    }



}
