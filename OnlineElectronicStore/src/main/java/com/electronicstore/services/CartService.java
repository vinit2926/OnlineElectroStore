package com.electronicstore.services;

import com.electronicstore.dtos.AddItemToCartRequest;
import com.electronicstore.dtos.CartDto;
import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.dtos.UserDto;

public interface CartService {

    //add items to cart
    //case 1 : cart for user is not available : we will create the cart then add the item
    //case 2 : cart available add the items to the cart

    CartDto addItemToCart(String userId, AddItemToCartRequest request);

    //remove item from cart
    void removeItemFromCart(String userId,int cartItem);

    void clearCart(String userId);

    CartDto getCartByUser(String userId);

    PageableResponse<CartDto> getAllCartsOfAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir);

}
