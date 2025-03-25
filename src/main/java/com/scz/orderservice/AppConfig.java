package com.scz.orderservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {


    @Bean(name = "auth-service-validate")
    public WebClient webClientAuthService(WebClient.Builder webClientBuilder)
    {
        return webClientBuilder
                .baseUrl("http://localhost:8087/shopcrazy/auth/v1/validate")
                .filter(new LoggingWebClientFilter())
                .build();
    }

    @Bean(name = "payment-service-create-payment")
    public WebClient webClientPymntService(WebClient.Builder webClientBuilder)
    {
        return webClientBuilder
                .baseUrl("http://localhost:8086/shopcrazy/payment/v1/create")
                .filter(new LoggingWebClientFilter())
                .build();
    }


}
