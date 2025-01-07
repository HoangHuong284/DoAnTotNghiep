package com.web.dto;

import com.web.enums.PayType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceRequest {

    private PayType payType;

    private String requestIdMomo;

    private String orderIdMomo;

    private String vnpOrderInfo;

    private String urlVnpay;

    private String address;

    private String fullName;

    private String phone;

    private Long wardId;

    private String note;

    private String voucherCode;
}
