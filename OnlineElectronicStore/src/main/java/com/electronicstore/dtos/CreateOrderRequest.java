package com.electronicstore.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateOrderRequest {

    //in URL but now JSON(body)
    @NotBlank(message = "CartId is required...")
    private String cartId;
    @NotBlank(message = "UserId is required...")
    private String userId;

    // in body
    private String orderStatus="PENDING";
    private String paymentStatus="NOTPAID";
    @NotBlank(message = "Billing address is required...")
    private String billingAddress;
    @NotBlank(message = "Phone number is required...")
    private String billingPhone;
    @NotBlank(message = "Billing name is required...")
    private String billingName;

}
