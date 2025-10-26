package com.telusko.SpringEcom.service;

import com.telusko.SpringEcom.model.Order;
import com.telusko.SpringEcom.model.OrderItem;
import com.telusko.SpringEcom.model.Product;
import com.telusko.SpringEcom.model.dto.OrderItemRequest;
import com.telusko.SpringEcom.model.dto.OrderItemResponse;
import com.telusko.SpringEcom.model.dto.OrderRequest;
import com.telusko.SpringEcom.model.dto.OrderResponse;
import com.telusko.SpringEcom.repo.OrderRepo;
import com.telusko.SpringEcom.repo.ProductRepo;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private VectorStore vectorStore;

    public OrderResponse postOrder(OrderRequest request) {

        Order order = new Order();
        String oid = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        order.setOrderId(oid);
        order.setCustomerName(request.customerName());
        order.setEmail(request.email());
        order.setStatus("PLACED");
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();

        for(OrderItemRequest item : request.items()){
            OrderItem orderItem = new OrderItem();
            Product product = productRepo.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException("Product Not Found"));
            product.setStockQuantity(product.getStockQuantity() - item.quantity());
            productRepo.save(product);

            String filter = String.format("productId==%s", item.productId());
            vectorStore.delete(filter);

            orderItem.setProduct(product);
            orderItem.setQuantity(item.quantity());
            orderItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(item.quantity())));
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        Order savedOrder = orderRepo.save(order);

        StringBuilder contentToEmbed = new StringBuilder();
        contentToEmbed.append("Order Summary: \n");
        contentToEmbed.append("OrderID: ").append(order.getOrderId()).append("\n");
        contentToEmbed.append("CustomerName: ").append(order.getCustomerName()).append("\n");
        contentToEmbed.append("Email: ").append(order.getEmail()).append("\n");
        contentToEmbed.append("Status: ").append(order.getStatus()).append("\n");
        contentToEmbed.append("OrderDate: ").append(order.getOrderDate()).append("\n");
        contentToEmbed.append("OrderItems: \n");

        for(OrderItem orderItem : orderItems){
            contentToEmbed.append("- ").append(orderItem.getProduct().getName()).append("\n");
            contentToEmbed.append(" x ").append(orderItem.getQuantity()).append("\n");
            contentToEmbed.append(" = $").append(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))).append("\n");
        }

        Document doc = new Document(
                UUID.randomUUID().toString(),
                contentToEmbed.toString(),
                Map.of("orderId", savedOrder.getOrderId())
        );

        vectorStore.add(List.of(doc));

        List<OrderItemResponse> responseItems = new ArrayList<>();

        for(OrderItem item: orderItems){
            OrderItemResponse itemResponse = new OrderItemResponse(
            item.getProduct().getName(),
            item.getQuantity(),
            item.getPrice()
            );
            responseItems.add(itemResponse);
        }


        OrderResponse orderResponse = new OrderResponse(
                savedOrder.getOrderId(),
                savedOrder.getCustomerName(),
                savedOrder.getEmail(),
                savedOrder.getStatus(),
                savedOrder.getOrderDate(),
                responseItems
        );



        return orderResponse;
    }

    public List<OrderResponse> getAllResponseOrders() {
        List<Order> orders = orderRepo.findAll();
        List<OrderResponse> response = new ArrayList<>();



        for(Order order: orders){
            List<OrderItemResponse> responseItems = new ArrayList<>();

            for(OrderItem item: order.getItems()){
                OrderItemResponse itemResponse = new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice()
                );
                responseItems.add(itemResponse);
            }

            OrderResponse orderResponse = new OrderResponse(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getEmail(),
                    order.getStatus(),
                    order.getOrderDate(),
                    responseItems
            );

            response.add(orderResponse);
        }



        return response;
    }
}
