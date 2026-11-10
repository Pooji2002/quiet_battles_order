package com.quietbattles.order_service.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private Long productId;
    private String productName;
    private String productSku;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private String size;

   public OrderItemResponse(
        Long productId,
        String productName,
        String productSku,
        BigDecimal unitPrice,
        Integer quantity,
        String size,
        BigDecimal subtotal) {

    this.productId = productId;
    this.productName = productName;
    this.productSku = productSku;
    this.unitPrice = unitPrice;
    this.quantity = quantity;
    this.size = size;
    this.subtotal = subtotal;
}

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public String getSize() {
        return size;
    }
}