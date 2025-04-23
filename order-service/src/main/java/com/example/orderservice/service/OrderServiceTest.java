package com.example.orderservice.service;

import com.example.orderservice.client.ProductServiceClient;
import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.dto.ProductDto;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItem;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;
    private Order order;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        OrderLineItemDto lineItemDto = OrderLineItemDto.builder()
                .productId("1")
                .quantity(2)
                .build();

        orderRequest = OrderRequest.builder()
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .orderLineItems(List.of(lineItemDto))
                .build();

        productDto = ProductDto.builder()
                .id("1")
                .name("Product 1")
                .description("Description 1")
                .price(new BigDecimal("10.99"))
                .stock(5)
                .build();

        OrderLineItem orderLineItem = OrderLineItem.builder()
                .productId("1")
                .productName("Product 1")
                .price(new BigDecimal("10.99"))
                .quantity(2)
                .build();

        order = Order.builder()
                .id("1")
                .customerName("John Doe")
                .customerEmail("john@example.com")
                .orderLineItems(List.of(orderLineItem))
                .totalAmount(new BigDecimal("21.98"))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();
    }

    @Test
    void shouldCreateOrder() {
        when(productServiceClient.getProductById("1")).thenReturn(productDto);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        assertNotNull(orderResponse);
        assertEquals("1", orderResponse.getId());
        assertEquals("John Doe", orderResponse.getCustomerName());
        assertEquals(OrderStatus.PLACED, orderResponse.getOrderStatus());
        
        verify(productServiceClient, times(1)).getProductById("1");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productServiceClient.getProductById("1")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStock() {
        // Update product stock to be less than requested quantity
        productDto.setStock(1);
        
        when(productServiceClient.getProductById("1")).thenReturn(productDto);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(orderRequest);
        });

        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    void shouldGetAllOrders() {
        Order order2 = Order.builder()
                .id("2")
                .customerName("Jane Smith")
                .customerEmail("jane@example.com")
                .orderLineItems(new ArrayList<>())
                .totalAmount(new BigDecimal("15.99"))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();

        when(orderRepository.findAll()).thenReturn(Arrays.asList(order, order2));

        List<OrderResponse> orderResponses = orderService.getAllOrders();

        assertEquals(2, orderResponses.size());
        assertEquals("1", orderResponses.get(0).getId());
        assertEquals("2", orderResponses.get(1).getId());
    }
    
    @Test
    void shouldGetOrderById() {
        when(orderRepository.findById("1")).thenReturn(Optional.of(order));
        
        OrderResponse orderResponse = orderService.getOrderById("1");
        
        assertEquals("1", orderResponse.getId());
        assertEquals("John Doe", orderResponse.getCustomerName());
    }
    
    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        when(orderRepository.findById("999")).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById("999");
        });
    }
    
    @Test
    void shouldGetOrdersByCustomerEmail() {
        when(orderRepository.findByCustomerEmail("john@example.com")).thenReturn(List.of(order));
        
        List<OrderResponse> orderResponses = orderService.getOrdersByCustomerEmail("john@example.com");
        
        assertEquals(1, orderResponses.size());
        assertEquals("John Doe", orderResponses.get(0).getCustomerName());
    }
}