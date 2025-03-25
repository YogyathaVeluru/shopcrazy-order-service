package com.scz.orderservice;

import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends ReactiveCouchbaseRepository<Order, String> {
}
