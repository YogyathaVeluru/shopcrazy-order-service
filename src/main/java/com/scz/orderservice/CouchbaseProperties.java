package com.scz.orderservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@Validated
@ConfigurationProperties("spring.couchbase")
public class CouchbaseProperties {

    private String hosts;
    private String username;
    private String password;
    private String bucketName;
}
