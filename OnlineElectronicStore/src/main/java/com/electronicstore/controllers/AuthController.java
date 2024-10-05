package com.electronicstore.controllers;

import com.electronicstore.dtos.JwtRequest;
import com.electronicstore.dtos.JwtResponse;
import com.electronicstore.dtos.UserDto;
import com.electronicstore.exceptions.BadApiRequestException;
import com.electronicstore.security.JwtHelper;
import com.electronicstore.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    @GetMapping("/current")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal){
        String name = principal.getName();
        return new ResponseEntity<>(mapper.map(userDetailsService.loadUserByUsername(name),UserDto.class), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request){
        this.doAuthenticate(request.getEmail(),request.getPassword());

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        //generate token
        String token = this.jwtHelper.generateToken(userDetails);

        UserDto userDto = mapper.map(userDetails,UserDto.class);
        JwtResponse response = JwtResponse.builder()
                .jwtToken(token)
                .user(userDto)
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    private void doAuthenticate(String email, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email,password);
        
        try
        {
            Authentication authenticate = manager.authenticate(authentication);
        }
        catch (BadCredentialsException e){
            throw new BadApiRequestException("Invalid username or password...");
        }
    }
}
