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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("Creating order for customer: {}", orderRequest.getCustomerName());
        
        List<OrderLineItem> orderLineItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderLineItemDto itemDto : orderRequest.getOrderLineItems()) {
            ProductDto productDto = productServiceClient.getProductById(itemDto.getProductId());
            
            if (productDto == null) {
                throw new RuntimeException("Product not found with id: " + itemDto.getProductId());
            }
            
            if (productDto.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + productDto.getName());
            }
            
            OrderLineItem orderLineItem = OrderLineItem.builder()
                    .productId(productDto.getId())
                    .productName(productDto.getName())
                    .price(productDto.getPrice())
                    .quantity(itemDto.getQuantity())
                    .build();
            
            orderLineItems.add(orderLineItem);
            
            // Calculate line item total and add to order total
            BigDecimal lineTotal = productDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }
        
        Order order = Order.builder()
                .customerName(orderRequest.getCustomerName())
                .customerEmail(orderRequest.getCustomerEmail())
                .orderLineItems(orderLineItems)
                .totalAmount(totalAmount)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PLACED)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());
        
        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    public OrderResponse getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        
        return mapToOrderResponse(order);
    }
    
    public List<OrderResponse> getOrdersByCustomerEmail(String email) {
        List<Order> orders = orderRepository.findByCustomerEmail(email);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }
    
    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .orderLineItems(order.getOrderLineItems())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .build();
    }
}