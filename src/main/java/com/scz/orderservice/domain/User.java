package com.scz.orderservice.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;

@Builder
@Getter
@Document
public class User {
    @Id
    private String id;
    private String username;
    private boolean guest;
}
