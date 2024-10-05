package com.electronicstore.controllers;

import com.electronicstore.dtos.*;
import com.electronicstore.exceptions.BadApiRequestException;
import com.electronicstore.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CartController {
    @Autowired
    private CartService cartService;

    //add item to cart
    @PostMapping("/{userId}")
    public ResponseEntity<CartDto> addItemToCart(@RequestBody AddItemToCartRequest addItemToCartRequest, @PathVariable String userId){
        CartDto cartDto = cartService.addItemToCart(userId, addItemToCartRequest);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    //remove item from the cart
//    @PUTMAPPING   we can use put mapping also here
    @DeleteMapping("/{userId}/items/{cartItemId}")
    public ResponseEntity<ApiResponseMessage> removeItemFromCart(@PathVariable String userId,@PathVariable int cartItemId){

        cartService.removeItemFromCart(userId,cartItemId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Item is removed...")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //clear cart
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> clearCart(@PathVariable String userId){

        cartService.clearCart(userId);
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message("Cart has been cleared...")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    //get card
    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCardByUserId(@PathVariable String userId){
        CartDto cartDto = cartService.getCartByUser(userId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    //get all card created additonal
    @GetMapping("/get")
    public ResponseEntity<PageableResponse<CartDto>> getAllCardsOfAllUser(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "cartId", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ){
        return new ResponseEntity<>(cartService.getAllCartsOfAllUsers(pageNumber,pageSize,sortBy,sortDir),HttpStatus.OK);
    }

}
