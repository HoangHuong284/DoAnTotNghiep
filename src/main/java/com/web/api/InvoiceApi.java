package com.web.api;

import com.web.dto.InvoiceRequest;
import com.web.entity.Category;
import com.web.entity.Invoice;
import com.web.enums.PayType;
import com.web.enums.Status;
import com.web.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin
public class InvoiceApi {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/user/create")
    public ResponseEntity<?> save(@RequestBody InvoiceRequest invoiceRequest){
        Invoice result = invoiceService.create(invoiceRequest);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/admin/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam("idInvoice") Long idInvoice, @RequestParam("status") Status status){
        Invoice result = invoiceService.updateStatus(idInvoice, status);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/employee/update-status")
    public ResponseEntity<?> updateStatusByEmp(@RequestParam("idInvoice") Long idInvoice, @RequestParam("status") Status status){
        Invoice result = invoiceService.updateStatus(idInvoice, status);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PostMapping("/user/cancel-invoice")
    public ResponseEntity<?> cancelInvoice(@RequestParam("idInvoice") Long idInvoice){
        Invoice result = invoiceService.cancelInvoice(idInvoice);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping("/user/find-by-user")
    public ResponseEntity<?> findByUser(){
        List<Invoice> result = invoiceService.findByUser();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/admin/find-all")
    public ResponseEntity<?> findAll(@RequestParam(value = "from",required = false) Date from,
                                     @RequestParam(value = "to",required = false) Date to,
                                     @RequestParam(value = "paytype",required = false) PayType payType,
                                     @RequestParam(value = "status",required = false) Status status, Pageable pageable){
        Page<Invoice> result = invoiceService.findAllFull(from, to,payType, status,pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/employee/find-all")
    public ResponseEntity<?> findAllByEmp(@RequestParam(value = "from",required = false) Date from,
                                     @RequestParam(value = "to",required = false) Date to,
                                     @RequestParam(value = "paytype",required = false) PayType payType,
                                     @RequestParam(value = "status",required = false) Status status, Pageable pageable){
        Page<Invoice> result = invoiceService.findAllFull(from, to,payType, status,pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/admin/find-all-by-date")
    public ResponseEntity<?> findAllByDate(@RequestParam(value = "from",required = false) Date from,
                                     @RequestParam(value = "to",required = false) Date to, Pageable pageable){
        Page<Invoice> result = invoiceService.findAll(from, to,pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/user/find-by-id")
    public ResponseEntity<?> findByUser(@RequestParam("idInvoice") Long idInvoice){
        Invoice result = invoiceService.findById(idInvoice);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/admin/find-by-id")
    public ResponseEntity<?> findByAdmin(@RequestParam("idInvoice") Long idInvoice){
        Invoice result = invoiceService.findByIdForAdmin(idInvoice);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/employee/find-by-id")
    public ResponseEntity<?> findByEmp(@RequestParam("idInvoice") Long idInvoice){
        Invoice result = invoiceService.findByIdForAdmin(idInvoice);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/admin/all-status")
    public ResponseEntity<?> allStatus(){
        List<Status> result = Arrays.stream(Status.class.getEnumConstants()).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/employee/all-status")
    public ResponseEntity<?> allStatusByEmp(){
        List<Status> result = Arrays.stream(Status.class.getEnumConstants()).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
