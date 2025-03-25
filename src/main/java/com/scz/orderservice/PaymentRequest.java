package com.scz.orderservice;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentRequest {

    private String payment_id;
    private String order_id;
    private Double amount;
}
