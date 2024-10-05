package com.electronicstore.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    //IDENTITY means auto increment in mysql
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartItemId;

    //unidirectional mapping means we can find product with cartItem
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;
    private int totalPrice;

    //mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;
}
