package com.web.service;

import com.web.config.Environment;
import com.web.dto.InvoiceRequest;
import com.web.entity.*;
import com.web.enums.PayType;
import com.web.enums.Status;
import com.web.exception.MessageException;
import com.web.models.QueryStatusTransactionResponse;
import com.web.processor.QueryTransactionStatus;
import com.web.repository.*;
import com.web.utils.MailService;
import com.web.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private HistoryPayRepository historyPayRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserUtils userUtils;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private InvoiceStatusRepository invoiceStatusRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private WardsRepository wardsRepository;

    @Autowired
    private VoucherService voucherService;

    
    public Invoice create(InvoiceRequest invoiceRequest) {
        if(invoiceRequest.getPayType().equals(PayType.PAYMENT_MOMO)){
            if(invoiceRequest.getRequestIdMomo() == null || invoiceRequest.getOrderIdMomo() == null){
                throw new MessageException("orderid and requestid require");
            }
            if(historyPayRepository.findByOrderIdAndRequestId(invoiceRequest.getOrderIdMomo(), invoiceRequest.getRequestIdMomo()).isPresent()){
                throw new MessageException("Đơn hàng đã được thanh toán");
            }
            Environment environment = Environment.selectEnv("dev");
            try {
                QueryStatusTransactionResponse queryStatusTransactionResponse = QueryTransactionStatus.process(environment, invoiceRequest.getOrderIdMomo(), invoiceRequest.getRequestIdMomo());
                System.out.println("qqqq-----------------------------------------------------------"+queryStatusTransactionResponse.getMessage());
                if(queryStatusTransactionResponse.getResultCode() != 0){
                    throw new MessageException("Đơn hàng chưa được thanh toán");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new MessageException("Đơn hàng chưa được thanh toán");
            }
        }
        List<Cart> carts = cartRepository.findByUser(userUtils.getUserWithAuthority().getId());
        if(carts.size() == 0){
            throw new MessageException("Bạn chưa có sản phẩm nào trong giỏ hàng");
        }
        for(Cart c : carts){
            Product product = c.getProduct();
            if (product.getQuantity() < c.getQuantity()){
                throw new MessageException("Sản phẩm "+c.getProduct().getName()+" chỉ còn lại "+product.getQuantity()+" sản phẩm");
            }
        }
        Double totalAmount = 0D;
        for(Cart c : carts){
            Double price = c.getProduct().getPrice();
            totalAmount += price * c.getQuantity();
        }

        User user = userUtils.getUserWithAuthority();
        Invoice invoice = new Invoice();
        invoice.setCreatedDate(new Date(System.currentTimeMillis()));
        invoice.setCreatedTime(new Time(System.currentTimeMillis()));
        invoice.setAddress(invoiceRequest.getAddress());
        invoice.setNote(invoiceRequest.getNote());
        invoice.setWards(wardsRepository.findById(invoiceRequest.getWardId()).get());
        invoice.setPhone(invoiceRequest.getPhone());
        invoice.setReceiverName(invoiceRequest.getFullName());
        invoice.setPayType(invoiceRequest.getPayType());
        invoice.setStatus(Status.DANG_CHO_XAC_NHAN);
        invoice.setUser(user);
        if(invoiceRequest.getVoucherCode() != null){
            if(!invoiceRequest.getVoucherCode().equals("null") && !invoiceRequest.getVoucherCode().equals("")){
                System.out.println("voucher use === "+invoiceRequest.getVoucherCode());
                Optional<Voucher> voucher = voucherService.findByCode(invoiceRequest.getVoucherCode(), totalAmount);
                if(voucher.isPresent()){
                    totalAmount = totalAmount - voucher.get().getDiscount();
                    invoice.setVoucher(voucher.get());
                }
            }
        }
        invoice.setTotalAmount(totalAmount);
        Invoice result = invoiceRepository.save(invoice);

        for(Cart c : carts){
            Product product = c.getProduct();
            InvoiceDetail invoiceDetail = new InvoiceDetail();
            invoiceDetail.setInvoice(result);
            invoiceDetail.setPrice(c.getProduct().getPrice());
            invoiceDetail.setQuantity(c.getQuantity());
            invoiceDetail.setProduct(product);
            invoiceDetailRepository.save(invoiceDetail);
            product.setQuantity(product.getQuantity() - c.getQuantity());
            if (product.getQuantitySold() == null)
                product.setQuantitySold(0);

            product.setQuantitySold(product.getQuantitySold() + c.getQuantity());
            productRepository.save(product);
        }

        if(invoiceRequest.getPayType().equals(PayType.PAYMENT_MOMO)){
            HistoryPay historyPay = new HistoryPay();
            historyPay.setInvoice(result);
            historyPay.setRequestId(invoiceRequest.getRequestIdMomo());
            historyPay.setOrderId(invoiceRequest.getOrderIdMomo());
            historyPay.setCreatedTime(new Time(System.currentTimeMillis()));
            historyPay.setCreatedDate(new Date(System.currentTimeMillis()));
            historyPay.setTotalAmount(totalAmount);
            historyPayRepository.save(historyPay);
        }

        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(result);
        invoiceStatus.setCreatedDate(LocalDateTime.now());
        invoiceStatus.setStatus(Status.DANG_CHO_XAC_NHAN);
        invoiceStatus.setUser(userUtils.getUserWithAuthority());
        invoiceStatusRepository.save(invoiceStatus);

        return result;
    }

    
    public Invoice updateStatus(Long invoiceId, Status status) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("invoice id not found");
        }
        if(invoiceStatusRepository.findByInvoiceAndStatus(status,invoiceId).isPresent()){
            throw new MessageException("Trạng thái đơn hàng này đã được cập nhật");
        }
        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(invoice.get());
        invoiceStatus.setCreatedDate(LocalDateTime.now());
        invoiceStatus.setStatus(status);
        invoiceStatus.setUser(userUtils.getUserWithAuthority());
        invoiceStatusRepository.save(invoiceStatus);
        invoice.get().setStatus(status);
        String str = "";
        User user = userUtils.getUserWithAuthority();
        Date d = new Date(System.currentTimeMillis());
        Time t = new Time(System.currentTimeMillis());
        String time = t.toString() +" ngày "+d.toString();
        if (status.equals(Status.DA_GUI)){
            str = "đã được gửi đi";
        }
        if (status.equals(Status.DA_XAC_NHAN)){
            str = "đã được xác nhận";
        }
        if (status.equals(Status.KHONG_NHAN_HANG)){
            str = "đã được hủy vì bạn không nhận hàng";
        }
        if (status.equals(Status.DA_NHAN)){
            str = "đã xác nhận được giao thành công";
        }
        if (status.equals(Status.DA_HUY)){
            str = "đã được hủy";
        }
        mailService.sendEmail(invoice.get().getUser().getEmail(), "Thông báo đơn hàng",
                "Đơn hàng: #"+invoice.get().getId()+" của bạn "+str+" bởi nhân viên "+user.getFullName() + " vào lúc "+time,
                false, true);
        return invoiceRepository.save(invoice.get());
    }

    
    public List<Invoice> findByUser() {
        User user = userUtils.getUserWithAuthority();
        List<Invoice> invoices = invoiceRepository.findByUser(user.getId());
        return invoices;
    }

    
    public Page<Invoice> findAll(Date from, Date to, Pageable pageable) {
        if(from == null || to == null){
            from = Date.valueOf("2000-01-01");
            to = Date.valueOf("2200-01-01");
        }
        Page<Invoice> page = invoiceRepository.findByDate(from, to,pageable);
        return page;
    }

    
    public Invoice cancelInvoice(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("invoice id not found");
        }
        if(invoice.get().getUser().getId() != userUtils.getUserWithAuthority().getId()){
            throw new MessageException("access denied");
        }
        if(invoice.get().getPayType().equals(PayType.PAYMENT_MOMO)){
            throw new MessageException("Đơn hàng đã được thanh toán, không thể hủy");
        }
        Status s = invoice.get().getStatus();
        if(s.equals(Status.DA_HUY) || s.equals(Status.DA_GUI) || s.equals(Status.DA_NHAN) || s.equals(Status.KHONG_NHAN_HANG)){
            throw new MessageException("không thể hủy hàng");
        }
        invoice.get().setStatus(Status.DA_HUY);
        Invoice result = invoiceRepository.save(invoice.get());

        List<InvoiceDetail> list  = invoiceDetailRepository.findByInvoiceId(invoiceId);
        for(InvoiceDetail i : list){
            i.getProduct().setQuantity(i.getQuantity() + i.getProduct().getQuantity());
            productRepository.save(i.getProduct());
        }
        InvoiceStatus invoiceStatus = new InvoiceStatus();
        invoiceStatus.setInvoice(invoice.get());
        invoiceStatus.setCreatedDate(LocalDateTime.now());
        invoiceStatus.setStatus(Status.DA_HUY);
        invoiceStatusRepository.save(invoiceStatus);
        return result;
    }

    
    public Invoice findById(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("invoice id not found");
        }
        if(invoice.get().getUser().getId() != userUtils.getUserWithAuthority().getId()){
            throw new MessageException("access denied");
        }
        return invoice.get();
    }

    
    public Invoice findByIdForAdmin(Long invoiceId) {
        Optional<Invoice> invoice = invoiceRepository.findById(invoiceId);
        if(invoice.isEmpty()){
            throw new MessageException("invoice id not found");
        }
        return invoice.get();
    }

    
    public Page<Invoice> findAllFull(Date from, Date to, PayType payType, Status status, Pageable pageable) {
        if(from == null || to == null){
            from = Date.valueOf("2000-01-01");
            to = Date.valueOf("2200-01-01");
        }
        Page<Invoice> page = null;
        if(payType == null && status == null){
            page = invoiceRepository.findByDate(from, to,pageable);
        }
        if(payType == null && status != null){
            page = invoiceRepository.findByDateAndStatus(from, to, status,pageable);
        }
        if(payType != null && status == null){
            page = invoiceRepository.findByDateAndPaytype(from, to,payType,pageable);
        }
        if(payType != null && status != null){
            page = invoiceRepository.findByDateAndPaytypeAndStatus(from, to,payType,status,pageable);
        }

        return page;
    }
}
