package com.web.dto;

import com.web.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class TrademarkProduct {

    private Long id;

    private String name;

    private List<Product> products = new ArrayList<>();
}
