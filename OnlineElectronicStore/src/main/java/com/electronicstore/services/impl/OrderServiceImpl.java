package com.electronicstore.services.impl;

import com.electronicstore.dtos.CreateOrderRequest;
import com.electronicstore.dtos.OrderDto;
import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.entities.*;
import com.electronicstore.exceptions.BadApiRequestException;
import com.electronicstore.exceptions.ResourceNotFoundException;
import com.electronicstore.helper.Helper;
import com.electronicstore.repositories.CartRepository;
import com.electronicstore.repositories.OrderRepository;
import com.electronicstore.repositories.UserRepository;
import com.electronicstore.services.OrderService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper mapper;


    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public OrderDto createOrder(CreateOrderRequest orderDto) {
        String userId = orderDto.getUserId();
        String cartId = orderDto.getCartId();

        //fetch user
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id..."));

        //fetch cart
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found with this id..."));

        if(cart.getUser().getUserId()!=user.getUserId()){
            throw new BadApiRequestException("UserId and CartId not belong to same...");
        }
        List<CartItem> cartItems = cart.getItems();

        if(cartItems.size()<=0){
            throw new BadApiRequestException("Invalid number of items in cart...."+cartItems.size());
        }

        Order order = Order.builder()
                .billingName(orderDto.getBillingName())
                .billingPhone(orderDto.getBillingPhone())
                .billingAddress(orderDto.getBillingAddress())
                .orderedDate(new Date())
                .deliveredDate(null)
                .paymentStatus(orderDto.getPaymentStatus())
                .orderStatus(orderDto.getOrderStatus())
                .orderId(UUID.randomUUID().toString())
                .user(user)
                .build();

        // orderItems , amount
        AtomicReference<Integer> orderAmount = new AtomicReference<>(0);
        List<OrderItem> orderItems = cartItems.stream().map(cartItem -> {

            //converting cartItem to orderItem
            OrderItem orderItem = OrderItem.builder()
                    .quantity(cartItem.getQuantity())
                    .product(cartItem.getProduct())
                    .totalPrice(cartItem.getQuantity() * cartItem.getProduct().getDiscountedPrice())
                    .order(order)
                    .build();

            orderAmount.set(orderAmount.get()+orderItem.getTotalPrice());
            return  orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        order.setOrderAmount(orderAmount.get());

        cart.getItems().clear();
        cartRepository.save(cart);

        Order savedOrder = orderRepository.save(order);
        return mapper.map(savedOrder,OrderDto.class);
    }

    @Override
    public void removeOrder(String orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order is not found with given id..."));
        //if order delete => all items of order will be deleted because
        // we have applied CASCADE.ALL
        orderRepository.delete(order);
    }

    @Override
    public List<OrderDto> getOrdersOfUser(String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id..."));

        List<Order> orders = orderRepository.findByUser(user);

        List<OrderDto> orderDtos = orders.stream().map(order -> {
            return mapper.map(order, OrderDto.class);
        }).collect(Collectors.toList());
        return orderDtos;
    }

    @Override
    public PageableResponse<OrderDto> getOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Order> page = orderRepository.findAll(pageable);
        return Helper.getPageableResponse(page,OrderDto.class);
    }

    @Override
    public OrderDto findOrderById(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found with given id..."));
        return mapper.map(order,OrderDto.class);
    }

    @Override
    public OrderDto updateOrder(OrderDto orderDto,String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException(("Order not found with given id")));

        order.setOrderStatus(orderDto.getOrderStatus());
        order.setBillingAddress(orderDto.getBillingAddress());
        order.setBillingName(orderDto.getBillingName());
        order.setPaymentStatus(orderDto.getPaymentStatus());
        Order savedOrder = orderRepository.save(order);
        return mapper.map(savedOrder,OrderDto.class);
    }
}
