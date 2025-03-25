package com.scz.orderservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("shopcrazy/order/v1")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    AuthService authService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    Producer producer;

    @GetMapping("get/order/{orderid}")
    public ResponseEntity<?> getOrder(@PathVariable("orderid") String orderid)
    {
        Order order = orderRepository.findById(orderid).block();
        return ResponseEntity.ok(order);
    }


    @PostMapping("create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest incomingOrderRequest,
                                         @RequestHeader("Authorization") String token,
                                         HttpServletRequest request,
                                         HttpServletResponse response) throws JsonProcessingException {

        // COOKIE VALIDATION LOGIC
        List<Cookie> cookieList = null;
        log.info("initiating cookie check");

        //Optional<String> healthStatusCookie = Optional.ofNullable(request.getHeader("health_status_cookie"));
        Cookie[] cookies = request.getCookies();
        if(cookies == null)
        {
            cookieList = new ArrayList<>();
        }
        else
        {
            // REFACTOR TO TAKE NULL VALUES INTO ACCOUNT
            cookieList = List.of(cookies);
        }
        log.info("cookie check complete");

        Order.OrderBuilder orderBuilder = Order.builder().orderId(incomingOrderRequest.getUsername() + UUID.randomUUID());

        if(cookieList.stream().filter(cookie -> cookie.getName().equals("order-service-stage-1")).toList().isEmpty()) // COOKIE_CHECK
        {
            log.info("Received request to create order for user {}", incomingOrderRequest.getUsername());
            ValidateResponse validateResponse = authService.validateToken(token);
            if(validateResponse.isValid())
            {
                log.info("Token is valid: {}", token);
                log.info("Proceeding to create order");
                Order order = orderBuilder
                        .orderId(validateResponse.getUsername() + UUID.randomUUID())
                        .productsInOrder(incomingOrderRequest.getRequestedProducts().stream()
                                .map(reqProduct -> Product.builder()
                                        .id(reqProduct.getId())
                                        .name(reqProduct.getName())
                                        .quantity(reqProduct.getQuantity())
                                        .price(reqProduct.getPrice()).build()
                                ).toList()
                        )
                        .status("PROCESSING").build();

                producer.publishOrderDatum(order.getOrderId(),
                                                "CREATE",
                        "Order Created Successfully with Order ID: " + order.getOrderId(),
                        order.getStatus(),
                        order.getPaymentId());
                orderRepository.save(order).subscribe(savedOrder -> {
                    // Optionally log the saved order
                    System.out.println("Order saved successfully: " + savedOrder);
                }, error -> {
                    // Handle errors appropriately
                    System.err.println("Error saving order: " + error.getMessage());
                });

                log.info("Order saved successfully: {}", order);

                log.info("Creating a New Payment Request");
                PaymentRequest paymentRequest = new PaymentRequest();
                paymentRequest.setOrder_id(order.getOrderId());
                paymentRequest.setPayment_id(null);
                paymentRequest.setAmount(order.calculateFinalAmount(order.getProductsInOrder()));
                log.info("Payment Request created successfully: {}", paymentRequest);

                log.info("Sending request to Payment Service");
                // create payment
                String responseKey = paymentService.createPayment(paymentRequest, token);
                log.info("Received the ResponeKey which will be sent as a Cookie to the Front-end");

                log.info("Setting up the Cookie for the Front-end");
                Cookie cookieStage1 = new Cookie("order-service-stage-1", responseKey);
                cookieStage1.setMaxAge(300);
                log.info("Cookie set up successfully");

                response.addCookie(cookieStage1);
                log.info("Cookie added to the outgoing response");
                log.info("Order created successfully: {} and request forwarded to Payment Service", order);
                return ResponseEntity.ok("STAGE 1: We have started processing your Order with Order ID: " + order.getOrderId());
            }
            else
            {
                log.info("Token is invalid: {}", token);
                return ResponseEntity.badRequest().body("Invalid token");
            }


        }
        else
        {

            // FOLLOW UP LOGIC
            log.info("found a relevant cookie.. initiating follow up logic");

            Cookie followup_cookie =  cookieList.stream().
                    filter(cookie -> cookie.getName().equals("order-service-stage-1")).findFirst().orElseThrow();

            String followup_cookie_key = followup_cookie.getValue();
            String cacheResponse = (String)redisTemplate.opsForValue().get(followup_cookie_key);

            String[] cacheResponseArray = cacheResponse.split(" ");

            if(cacheResponseArray[0].equals("stage1"))
            {
                log.info("Request still under process...");

                return ResponseEntity.ok("Request still under process...");
            }
            else if(cacheResponseArray[0].equals("paymentid:orderid"))
            {
                return ResponseEntity.ok("Order Created Successfully with Order ID: " + orderBuilder.build().getOrderId() + " and Payment ID: " + cacheResponseArray[1]);
            }
            else
            {
                return ResponseEntity.ok("Error Processing the Order");
            }


        }




    }

}
