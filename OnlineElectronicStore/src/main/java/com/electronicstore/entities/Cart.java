package com.electronicstore.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Cart {

    @Id
    private String cartId;

    private Date createdAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    //mapping cart items
    //bidirection mapping between Cart and cartItem
    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
}
