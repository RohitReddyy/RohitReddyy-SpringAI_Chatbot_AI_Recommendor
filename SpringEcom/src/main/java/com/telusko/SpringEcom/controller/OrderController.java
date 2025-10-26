package com.telusko.SpringEcom.controller;

import com.telusko.SpringEcom.model.dto.OrderRequest;
import com.telusko.SpringEcom.model.dto.OrderResponse;
import com.telusko.SpringEcom.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("orders/place")
    public ResponseEntity<OrderResponse> postOrder(@RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.postOrder(orderRequest);
        return new ResponseEntity<>(orderResponse,HttpStatus.CREATED);
    }

    @GetMapping("orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        List<OrderResponse> lst = orderService.getAllResponseOrders();
        return new ResponseEntity<>(lst,HttpStatus.OK);
    }
}
