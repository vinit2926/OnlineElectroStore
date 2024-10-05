package com.electronicstore.services;

import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.dtos.UserDto;
import com.electronicstore.entities.User;

import java.io.IOException;
import java.util.List;

public interface UserService {

    //create
    UserDto createUser(UserDto userDto);

    //update
    UserDto updateUser(UserDto userDto,String userId);

    //delete
    void deleteuser(String userId) throws IOException;

    //get all users
    PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);

    //get single user by id
    UserDto getUserById(String userId);

    //get single user by email
    UserDto getUserByEmail(String email);

    //search user
     List<UserDto> searchUser(String keyword);

     List<UserDto> setPasswordByEncode();

}
