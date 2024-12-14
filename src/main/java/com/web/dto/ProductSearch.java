package com.web.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProductSearch {

    private List<Long> categoryIds = new ArrayList<>();
    Double minPrice;
    Double maxPrice;
}
