package com.scz.orderservice.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.List;

@Builder
@Getter
@Document
public class Cart {
    @Id
    private String id;
    private String userId;
    private List<Product> items;
}
