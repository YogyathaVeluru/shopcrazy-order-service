package com.scz.orderservice.infrastructure;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.core.convert.CouchbaseCustomConversions;

import java.util.Collections;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
public class CouchbaseConfiguration extends AbstractCouchbaseConfiguration {

    private final CouchbaseProperties properties;

    @Bean
    @Primary
    public CustomConversions couchbaseCustomConversions() {
        return new CouchbaseCustomConversions(Collections.emptyList());
    }

    @Override
    public String getConnectionString() {
        return properties.getHosts();
    }

    @Override
    public String getUserName() {
        return properties.getUsername();
    }

    @Override
    public String getPassword() {
        return properties.getPassword();
    }

    @Override
    public String getBucketName() {
        return properties.getBucketName();
    }

}
