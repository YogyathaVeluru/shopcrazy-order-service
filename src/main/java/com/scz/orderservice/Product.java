package com.scz.orderservice;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@Builder
@Getter
public class Product {

    @Id
    private String id;

    private String name;

    private int quantity;

    private double price;


}
