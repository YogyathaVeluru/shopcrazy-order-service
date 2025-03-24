package com.scz.orderservice.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Builder
@Getter
@Document
public class Product {
    @Id
    private String id;

    private String name;

    private int quantity;

    private double price;
}
