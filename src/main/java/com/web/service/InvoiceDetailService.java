package com.web.service;

import com.web.entity.InvoiceDetail;
import com.web.repository.InvoiceDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceDetailService{

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;


    public List<InvoiceDetail> findByInvoice(Long idInvoice) {
        List<InvoiceDetail> list = invoiceDetailRepository.findByInvoiceId(idInvoice);
        return list;
    }
}
