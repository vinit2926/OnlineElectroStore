package com.electronicstore.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "orders")
public class Order {

    @Id
    private String orderId;

    //Pending,Delevered,Dispatched => For Admin
    // here we can use enum
    private String orderStatus;

    //NOT PAID, PAID
    // we can use enum or boolean false=>NOT PAID,true=>PAID
    private String paymentStatus;
    private int orderAmount;
    @Column(length = 1000)

    private String billingAddress;
    private String billingPhone;
    private String billingName;

    private Date orderedDate;
    private Date deliveredDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order",fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

}
