package com.electronicstore.services;

import com.electronicstore.dtos.CreateOrderRequest;
import com.electronicstore.dtos.OrderDto;
import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.entities.Order;

import java.util.List;

public interface OrderService {

    //create order
//    OrderDto createOrder(CreateOrderRequest orderDto, String userId, String cartId);
    OrderDto createOrder(CreateOrderRequest orderDto);

    //remove order
    void removeOrder(String orderId);

    //get orders of user
    List<OrderDto> getOrdersOfUser(String userId);

    //get all orders
    PageableResponse<OrderDto> getOrders(int pageNumber,int pageSize,String sortBy,String sortDir);

    //other services

    //additional created for testing
    OrderDto findOrderById(String orderId);

    OrderDto updateOrder(OrderDto orderDto,String orderId);

}
