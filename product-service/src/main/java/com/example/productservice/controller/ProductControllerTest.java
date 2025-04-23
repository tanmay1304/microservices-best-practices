package com.example.productservice.controller;

import com.example.productservice.dto.ProductRequest;
import com.example.productservice.dto.ProductResponse;
import com.example.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .name("Test Product")
                .description("Test Description")
                .price(new BigDecimal("10.99"))
                .stock(100)
                .build();

        doNothing().when(productService).createProduct(any(ProductRequest.class));

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidData() throws Exception {
        ProductRequest productRequest = ProductRequest.builder()
                .name("")  // Invalid: Empty name
                .description("Test Description")
                .price(new BigDecimal("-10.99"))  // Invalid: Negative price
                .stock(-10)  // Invalid: Negative stock
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllProducts() throws Exception {
        List<ProductResponse> productResponses = Arrays.asList(
                new ProductResponse("1", "Product 1", "Description 1", new BigDecimal("10.99"), 100),
                new ProductResponse("2", "Product 2", "Description 2", new BigDecimal("20.99"), 50)
        );

        when(productService.getAllProducts()).thenReturn(productResponses);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }
    
    @Test
    void shouldGetProductById() throws Exception {
        ProductResponse productResponse = new ProductResponse("1", "Product 1", "Description 1", new BigDecimal("10.99"), 100);
        
        when(productService.getProductById("1")).thenReturn(productResponse);
        
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Product 1"));
    }
}