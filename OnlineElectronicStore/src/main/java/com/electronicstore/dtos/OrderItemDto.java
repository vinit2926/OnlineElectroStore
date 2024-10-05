package com.electronicstore.dtos;

import com.electronicstore.entities.Order;
import com.electronicstore.entities.Product;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OrderItemDto {

    private int orderItemId;
    private int quantity;

    private int totalPrice;
    private ProductDto product;
//    private Order order;
}
