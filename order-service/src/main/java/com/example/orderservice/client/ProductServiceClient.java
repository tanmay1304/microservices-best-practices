package com.example.orderservice.client;

import com.example.orderservice.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final WebClient webClient;
    
    @Value("${product-service.url}")
    private String productServiceUrl;
    
    public ProductDto getProductById(String productId) {
        log.info("Fetching product information for product ID: {}", productId);
        
        return webClient.get()
                .uri(productServiceUrl + "/api/products/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .onErrorResume(e -> {
                    log.error("Error fetching product with ID: {}, Error: {}", productId, e.getMessage());
                    return Mono.empty();
                })
                .block();
    }
}