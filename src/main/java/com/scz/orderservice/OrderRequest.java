package com.scz.orderservice;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequest {
    private String username;
    private List<RequestedProduct> requestedProducts;
}
