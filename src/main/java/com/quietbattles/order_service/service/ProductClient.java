
package com.quietbattles.order_service.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.quietbattles.order_service.dto.ProductResponse;

@Component
public class ProductClient {

    private final RestClient productRestClient;
    private final String internalServiceKey;

    public ProductClient(
            RestClient productRestClient,
            @Value("${app.internal.service-key}") String internalServiceKey) {

        this.productRestClient = productRestClient;
        this.internalServiceKey = internalServiceKey;
    }

    public ProductResponse getProduct(Long productId, String token) {
        try {
            return productRestClient.get()
                    .uri("/api/products/{id}", productId)
                    .header("Authorization", token)
                    .retrieve()
                    .body(ProductResponse.class);

        } catch (RestClientResponseException ex) {
            throw new IllegalArgumentException(
                    "Unable to retrieve product with ID: " + productId
                    + ". Product Service returned: " + ex.getStatusCode());
        }
    }

    public ProductResponse reserveStock(
            Long productId, Integer quantity, String token) {

        try {
            return productRestClient.post()
                    .uri("/api/products/{id}/reserve-stock", productId)
                    .header("X-INTERNAL-SERVICE-KEY", internalServiceKey)
                    .body(Map.of("quantity", quantity))
                    .retrieve()
                    .body(ProductResponse.class);

        } catch (RestClientResponseException ex) {
            throw new IllegalArgumentException(
                    "Unable to reserve stock for product ID: " + productId
                    + ". Product Service returned: " + ex.getStatusCode());
        }
    }

    public void releaseStock(
            Long productId, Integer quantity, String token) {

        try {
            productRestClient.post()
                    .uri("/api/products/{id}/release-stock", productId)
                    .header("X-INTERNAL-SERVICE-KEY", internalServiceKey)
                    .body(Map.of("quantity", quantity))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException ex) {
            throw new IllegalArgumentException(
                    "Unable to release stock for product ID: " + productId
                    + ". Product Service returned: " + ex.getStatusCode());
        }
    }
}
