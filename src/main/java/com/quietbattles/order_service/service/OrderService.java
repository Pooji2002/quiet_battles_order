
package com.quietbattles.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.quietbattles.order_service.dto.*;
import com.quietbattles.order_service.entity.Order;
import com.quietbattles.order_service.entity.OrderItem;
import com.quietbattles.order_service.repository.OrderRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderService(
            OrderRepository orderRepository,
            ProductClient productClient) {

        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    @Transactional
    public OrderResponse createOrder(
            OrderRequest request, Long userId, String token) {

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");

        BigDecimal total = BigDecimal.ZERO;

        // Keep track of successfully reserved stock
        List<Reservation> reservations = new ArrayList<>();

        try {

            for (OrderItemRequest itemRequest : request.getItems()) {

                // Reserve stock before adding the order item
                ProductResponse product = productClient.reserveStock(
                        itemRequest.getProductId(),
                        itemRequest.getQuantity(),
                        token);

                if (product == null
                        || !"ACTIVE".equalsIgnoreCase(product.getStatus())) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Product is not available: "
                                    + itemRequest.getProductId());
                }

                // Remember this reservation for possible rollback
                reservations.add(new Reservation(
                        product.getId(),
                        itemRequest.getQuantity()));

                BigDecimal subtotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(
                                itemRequest.getQuantity()));

                OrderItem item = new OrderItem();

                item.setProductId(product.getId());
                item.setProductName(product.getName());
                item.setProductSku(product.getSku());
                item.setUnitPrice(product.getPrice());
                item.setQuantity(itemRequest.getQuantity());
                item.setSize(itemRequest.getSize());
                item.setSubtotal(subtotal);

                order.addItem(item);

                total = total.add(subtotal);
            }

            order.setTotalAmount(total);

            Order savedOrder = orderRepository.save(order);

            return mapToResponse(savedOrder);

        } catch (RuntimeException ex) {

            // If order creation fails, restore previously reserved stock
            for (Reservation reservation : reservations) {

                try {
                    productClient.releaseStock(
                            reservation.productId(),
                            reservation.quantity(),
                            token);

                } catch (RuntimeException releaseException) {
                    ex.addSuppressed(releaseException);
                }
            }

            throw ex;
        }
    }

    public List<OrderResponse> getMyOrders(Long userId) {

        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, Long userId) {

        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Order not found"));

        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getProductName(),
                        item.getProductSku(),
                        item.getUnitPrice(),
                        item.getQuantity(),
                        item.getSize(),
                        item.getSubtotal()))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemResponses);
    }

    // Stores which product stock was reserved
    private record Reservation(Long productId, Integer quantity) {
    }
    

@Transactional
public OrderResponse cancelOrder(
        Long orderId, Long userId, String token) {

    Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Order not found"));

    // Prevent restoring stock twice
    if ("CANCELLED".equalsIgnoreCase(order.getStatus())) {
        return mapToResponse(order);
    }

    // Only pending orders can be cancelled
    if (!"PENDING".equalsIgnoreCase(order.getStatus())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only pending orders can be cancelled");
    }

    // Restore stock for every product in the order
    for (OrderItem item : order.getItems()) {

        productClient.releaseStock(
                item.getProductId(),
                item.getQuantity(),
                token);
    }

    // Update order status
    order.setStatus("CANCELLED");

    Order cancelledOrder = orderRepository.save(order);

    return mapToResponse(cancelledOrder);
}

}
