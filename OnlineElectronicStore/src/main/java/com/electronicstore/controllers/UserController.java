package com.electronicstore.controllers;


import com.electronicstore.dtos.ApiResponseMessage;
import com.electronicstore.dtos.ImageResponse;
import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.dtos.UserDto;
import com.electronicstore.services.FileService;
import com.electronicstore.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

     @Autowired
     private UserService userService;

     @Autowired
     private FileService fileService;

     @Value("${users.profile.image.path}")
     private String imageUploadPath;

     private Logger logger = LoggerFactory.getLogger(UserController.class);

     //create
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto){
        UserDto userDto1 = userService.createUser(userDto);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }

    //update
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String userId,@Valid @RequestBody UserDto userDto){
        UserDto updatedUserDto = userService.updateUser(userDto, userId);
        return new ResponseEntity<>(updatedUserDto,HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) throws IOException {
        userService.deleteuser(userId);
        ApiResponseMessage message = ApiResponseMessage.builder()
                .message("User is deleted successfully")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    //get all
    @GetMapping
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
            ){
        return new ResponseEntity<>(userService.getAllUser(pageNumber,pageSize,sortBy,sortDir),HttpStatus.OK);
    }

    //get single
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String userId){
        return new ResponseEntity<>(userService.getUserById(userId),HttpStatus.OK);
    }

    //get by email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
        return new ResponseEntity<>(userService.getUserByEmail(email),HttpStatus.OK);
    }

    //search user
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords){
        return new ResponseEntity<>(userService.searchUser(keywords),HttpStatus.OK);
    }

    //upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @PathVariable String userId, @RequestParam("userImage") MultipartFile image
            ) throws IOException {
        String imageName = fileService.uploadFile(image, imageUploadPath);

        logger.info("IMAGE UPLODING PATH : {}",imageUploadPath);
        UserDto user = userService.getUserById(userId);

        user.setImageName(imageName);
        UserDto userDto = userService.updateUser(user, userId);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .message("User Image uploaded successfully...")
                .success(true)
                .status(HttpStatus.CREATED)
                .build();
        return new ResponseEntity<>(imageResponse,HttpStatus.CREATED);

    }

    //serve user image
    @GetMapping("/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        UserDto user = userService.getUserById(userId);
        logger.info("User Image Name : {}",user.getImageName());
        logger.info("IMAGE Upload Path : {}",imageUploadPath);

        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());

        response.setContentType(MediaType.IMAGE_PNG_VALUE);

        StreamUtils.copy(resource,response.getOutputStream());
    }


    @PutMapping("/setPassword")
    public ResponseEntity<List<UserDto>> setPassword1234ByEndode(){
        List<UserDto> userDtos = userService.setPasswordByEncode();
        return new ResponseEntity<>(userDtos,HttpStatus.OK);
    }



}
