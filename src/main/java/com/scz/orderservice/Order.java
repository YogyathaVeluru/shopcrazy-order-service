package com.scz.orderservice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Builder
@Setter
public class Order
{

    @Id
    private String orderId;
    private String username;
    List<Product> productsInOrder;
    private String paymentId;
    private String status;

    public double calculateFinalAmount(List<Product> products) {
        return products.stream()
        .mapToDouble(product -> product.getPrice() * product.getQuantity())
        .sum();
    }

}
