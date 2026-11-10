package com.quietbattles.order_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.quietbattles.order_service.dto.OrderRequest;
import com.quietbattles.order_service.dto.OrderResponse;
import com.quietbattles.order_service.security.JwtService;
import com.quietbattles.order_service.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtService jwtService;

    public OrderController(OrderService orderService,
            JwtService jwtService) {
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtService.extractUserId(token.substring(7));

        OrderResponse response = orderService.createOrder(
                request, userId, token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            @RequestHeader("Authorization") String token) {

        Long userId = jwtService.extractUserId(token.substring(7));

        return ResponseEntity.ok(orderService.getMyOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader("Authorization") String token) {

        Long userId = jwtService.extractUserId(token.substring(7));

        return ResponseEntity.ok(
                orderService.getOrderById(orderId, userId));
    }
    

@PatchMapping("/{orderId}/cancel")
public ResponseEntity<OrderResponse> cancelOrder(
        @PathVariable Long orderId,
        @RequestHeader("Authorization") String token) {

    Long userId = jwtService.extractUserId(token.substring(7));

    OrderResponse response = orderService.cancelOrder(
            orderId, userId, token);

    return ResponseEntity.ok(response);
}

}