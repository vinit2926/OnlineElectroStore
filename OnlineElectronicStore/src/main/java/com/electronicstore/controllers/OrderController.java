package com.electronicstore.controllers;

import com.electronicstore.dtos.ApiResponseMessage;
import com.electronicstore.dtos.CreateOrderRequest;
import com.electronicstore.dtos.OrderDto;
import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //create
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest){
        OrderDto order = orderService.createOrder(createOrderRequest);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    //update order
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto> updateOrder(@RequestBody OrderDto orderDto,@PathVariable String orderId){

        OrderDto orderDto1 = orderService.updateOrder(orderDto, orderId);
        return new ResponseEntity<>(orderDto1,HttpStatus.OK);
    }

    //remove order
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponseMessage> removeOrder(@PathVariable String orderId){
           orderService.removeOrder(orderId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Order remove successfully...")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    //get orders of user
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable String userId){
        List<OrderDto> ordersOfUser = orderService.getOrdersOfUser(userId);
        return new ResponseEntity<>(ordersOfUser,HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<PageableResponse<OrderDto>> getAllOrders(
            @RequestParam(value = "pageNumber",defaultValue = "0",required = false) int pageNumber,
            @RequestParam(value = "pageSize",defaultValue = "10",required = false) int pageSize,
            @RequestParam(value = "sortBy",defaultValue = "orderedDate",required = false) String sortBy,
            @RequestParam(value = "sortDir",defaultValue = "asc",required = false) String sortDir

    ){

        PageableResponse<OrderDto> orders = orderService.getOrders(pageNumber, pageSize, sortBy, sortDir);

        return new ResponseEntity<>(orders,HttpStatus.OK);
    }

    //find order by id
    @GetMapping("/findOrder/{orderId}")
    public ResponseEntity<OrderDto> getOrderByOrderId(@PathVariable String orderId){
        OrderDto orderById = orderService.findOrderById(orderId);
        return new ResponseEntity<>(orderById,HttpStatus.OK);
    }
}
