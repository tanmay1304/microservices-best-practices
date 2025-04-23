package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.OrderLineItem;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrder() throws Exception {
        OrderLineItemDto lineItemDto = OrderLineItemDto.builder()
                .productId("1")
                .quantity(2)
                .build();

        OrderRequest orderRequest = OrderRequest.builder()
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .orderLineItems(List.of(lineItemDto))
                .build();

        OrderLineItem orderLineItem = OrderLineItem.builder()
                .productId("1")
                .productName("Product 1")
                .price(new BigDecimal("10.99"))
                .quantity(2)
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .id("1")
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .orderLineItems(List.of(orderLineItem))
                .totalAmount(new BigDecimal("21.98"))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidData() throws Exception {
        OrderRequest orderRequest = OrderRequest.builder()
                .customerName("")  // Invalid: Empty name
                .customerEmail("invalid-email")  // Invalid email format
                .orderLineItems(new ArrayList<>())  // Invalid: Empty order items
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllOrders() throws Exception {
        OrderLineItem orderLineItem1 = OrderLineItem.builder()
                .productId("1")
                .productName("Product 1")
                .price(new BigDecimal("10.99"))
                .quantity(2)
                .build();

        OrderLineItem orderLineItem2 = OrderLineItem.builder()
                .productId("2")
                .productName("Product 2")
                .price(new BigDecimal("15.99"))
                .quantity(1)
                .build();

        OrderResponse orderResponse1 = OrderResponse.builder()
                .id("1")
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .orderLineItems(List.of(orderLineItem1))
                .totalAmount(new BigDecimal("21.98"))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();

        OrderResponse orderResponse2 = OrderResponse.builder()
                .id("2")
                .customerName("Jane Smith")
                .customerEmail("jane@example.com")
                .orderLineItems(List.of(orderLineItem2))
                .totalAmount(new BigDecimal("15.99"))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();

        when(orderService.getAllOrders()).thenReturn(Arrays.asList(orderResponse1, orderResponse2));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));
    }
    
    @Test
    void shouldGetOrderById() throws Exception {
        OrderLineItem orderLineItem = OrderLineItem.builder()
                .productId("1")
                .productName("Product 1")
                .price(new BigDecimal("10.99"))
                .quantity(2)
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .id("1")
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .orderLineItems(List.of(orderLineItem))
                .totalAmount(new BigDecimal("21.98"))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();
        
        when(orderService.getOrderById("1")).thenReturn(orderResponse);
        
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }
    
    @Test
    void shouldGetOrdersByCustomerEmail() throws Exception {
        OrderLineItem orderLineItem = OrderLineItem.builder()
                .productId("1")
                .productName("Product 1")
                .price(new BigDecimal("10.99"))
                .quantity(2)
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .id("1")
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .orderLineItems(List.of(orderLineItem))
                .totalAmount(new BigDecimal("21.98"))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();
        
        when(orderService.getOrdersByCustomerEmail("john@example.com")).thenReturn(List.of(orderResponse));
        
        mockMvc.perform(get("/api/orders/customer/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerEmail").value("john@example.com"));
    }
}