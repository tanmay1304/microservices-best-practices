package com.example.productservice.service;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;
    private Product product;

    @BeforeEach
    void setUp() {
        productRequest = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10.99"))
                .stock(100)
                .build();

        product = Product.builder()
                .id("1")
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10.99"))
                .stock(100)
                .build();
    }

    @Test
    void shouldCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.createProduct(productRequest);

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void shouldGetAllProducts() {
        Product product2 = Product.builder()
                .id("2")
                .name("Test Product 2")
                .description("Test Description 2")
                .price(new BigDecimal("20.99"))
                .stock(50)
                .build();

        when(productRepository.findAll()).thenReturn(Arrays.asList(product, product2));

        List<ProductResponse> productResponses = productService.getAllProducts();

        assertEquals(2, productResponses.size());
        assertEquals("Test Product", productResponses.get(0).getName());
        assertEquals("Test Product 2", productResponses.get(1).getName());
    }
    
    @Test
    void shouldGetProductById() {
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        
        ProductResponse productResponse = productService.getProductById("1");
        
        assertEquals("1", productResponse.getId());
        assertEquals("Test Product", productResponse.getName());
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepository.findById("999")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            productService.getProductById("999");
        });
    }
}