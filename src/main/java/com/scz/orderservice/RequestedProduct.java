package com.scz.orderservice;

import lombok.Getter;
import org.springframework.data.annotation.Id;


@Getter
public class RequestedProduct {
    @Id
    private String id;

    private String name;

    private int quantity;

    private double price;
}
