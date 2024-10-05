package com.electronicstore.services.impl;

import com.electronicstore.dtos.*;
import com.electronicstore.entities.Cart;
import com.electronicstore.entities.CartItem;
import com.electronicstore.entities.Product;
import com.electronicstore.entities.User;
import com.electronicstore.exceptions.BadApiRequestException;
import com.electronicstore.exceptions.ResourceNotFoundException;
import com.electronicstore.helper.Helper;
import com.electronicstore.repositories.CartItemRepository;
import com.electronicstore.repositories.CartRepository;
import com.electronicstore.repositories.ProductRepository;
import com.electronicstore.repositories.UserRepository;
import com.electronicstore.services.CartService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper mapper;

    private Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Override
    public CartDto addItemToCart(String userId, AddItemToCartRequest request) {

        int quantity = request.getQuantity();
        String productId = request.getProductId();

        if(quantity<=0){
            throw new BadApiRequestException("Requested quantity is not valid...");
        }

        //fetch the product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with given id..."));

        //fetch the user from db
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(("User not found with give id...")));
        Cart cart = null;

        try
        {
            cart = cartRepository.findByUser(user).get();
        }
        catch(NoSuchElementException e){
                 cart = new Cart();
                 cart.setCartId(UUID.randomUUID().toString());
                 cart.setCreatedAt(new Date());
        }

        //perform cart opertion
        //if cart item already present : then update
//        AtomicReference<Boolean> updated = new AtomicReference<>(false);
        //boolean updated=false; we cannot use it should be final
        List<CartItem> items = cart.getItems();

//        List<CartItem> updatedItems = items.stream().map(item -> {
//            if (item.getProduct().getProductId().equals(productId)) {
//                //item already present in cart
//                int productQuantity = product.getQuantity();
////                logger.info("PRODUCT QUANTITY : {}",productQuantity);
////                logger.info("USER QUANTITY DEMAND: {}",quantity);
//                if(productQuantity>=quantity){
//                    item.setQuantity(quantity);
//                }
//                else {
//                    throw new BadApiRequestException("Only "+productQuantity+" quantity are available...");
//                }
//                item.setTotalPrice(quantity*product.getDiscountedPrice());
//                updated.set(true);
//            }
//            return item;
//        }).collect(Collectors.toList());
        boolean updated = false;
        for (CartItem item : items) {
            if (item.getProduct().getProductId().equals(productId)) {
                int productQuantity = product.getQuantity();
                if (productQuantity >= quantity) {
                    item.setQuantity(quantity);
                    item.setTotalPrice(quantity * product.getDiscountedPrice());
                } else {
                    throw new BadApiRequestException("Only " + productQuantity + " quantity are available...");
                }
                updated = true;
                break; // Exit loop early if item is updated
            }
        }
        cart.setItems(items);

        //create items
        if(updated==false){

            int pQuantity = product.getQuantity();

            if(pQuantity<quantity){
                throw new  BadApiRequestException("You can add only "+pQuantity+" quantity...");
            }
            CartItem cartItem = CartItem.builder()
                    .quantity(quantity)
                    .totalPrice(quantity * product.getDiscountedPrice())
                    .cart(cart)
                    .product(product)
                    .build();
            cart.getItems().add(cartItem);
        }

        cart.setUser(user);

        Cart updatedCart = cartRepository.save(cart);
        return mapper.map(updatedCart,CartDto.class);
    }

    @Override
    public void removeItemFromCart(String userId, int cartItem) {

        CartItem cartItem1 = cartItemRepository.findById(cartItem).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem1);
    }


   @Override
    public void clearCart(String userId) {
        //fetch the user from db
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(("User not found with give id...")));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user not found"));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public CartDto getCartByUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(("User not found with give id...")));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new ResourceNotFoundException("Cart of given user not found"));
        logger.info("USER CART IS : ",user);
        logger.info("USER CART IS : ",cart);
        return mapper.map(cart,CartDto.class);
    }

    @Override
    public PageableResponse<CartDto> getAllCartsOfAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {

            Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
            //pageNumber default starts from 0
            Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Cart> page = cartRepository.findAll(pageable);


        PageableResponse<CartDto> response = Helper.getPageableResponse(page, CartDto.class);
        return response;

    }

}
