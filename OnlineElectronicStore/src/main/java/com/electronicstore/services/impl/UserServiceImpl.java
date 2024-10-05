package com.electronicstore.services.impl;

import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.dtos.UserDto;
import com.electronicstore.entities.Role;
import com.electronicstore.entities.User;
import com.electronicstore.exceptions.ResourceNotFoundException;
import com.electronicstore.helper.Helper;
import com.electronicstore.repositories.RoleRepository;
import com.electronicstore.repositories.UserRepository;
import com.electronicstore.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${users.profile.image.path}")
    private String imagePath;

    @Value("${normal.role.id}")
    String normalRoleId;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public UserDto createUser(UserDto userDto) {
        //generate unique id
        String randomId = UUID.randomUUID().toString();
        userDto.setUserId(randomId);
        //set encoding password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User user = dtoToEntity(userDto);

        //fetch role of normal user an set it to user
        Role role = roleRepository.findById(normalRoleId).get();
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
        UserDto newDto = entityToDto(savedUser);
        return newDto;
    }

    private UserDto entityToDto(User savedUser) {
//        UserDto userDto = UserDto.builder()
//                .userId(savedUser.getUserId())
//                .name(savedUser.getName())
//                .email(savedUser.getEmail())
//                .password(savedUser.getPassword())
//                .about(savedUser.getAbout())
//                .gender(savedUser.getGender())
//                .imageName(savedUser.getImageName())
//                .build();
        return mapper.map(savedUser,UserDto.class);
    }

    private User dtoToEntity(UserDto userDto) {
//        User user = User.builder()
//                .userId(userDto.getUserId())
//                .name(userDto.getName())
//                .email(userDto.getEmail())
//                .password(userDto.getPassword())
//                .about(userDto.getAbout())
//                .gender(userDto.getGender())
//                .imageName(userDto.getImageName())
//                .build();
        return mapper.map(userDto,User.class);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setGender(userDto.getGender());
        user.setPassword(userDto.getPassword());
        user.setImageName(userDto.getImageName());

        User updatedUser = userRepository.save(user);
        UserDto updatedDto = entityToDto(updatedUser);
        return updatedDto;
    }

    @Override
    public void deleteuser(String userId)  {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));

        //delete user image
        // images/user/abc.png
        String fullPath = imagePath+user.getImageName();
        try
        {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }
        catch (NoSuchFileException e){
             logger.info("User image not found in folder");
             e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        //pageNumber default starts from 0
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<User> page = userRepository.findAll(pageable);

        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);
        return response;
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
       return entityToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with given email"));
        return entityToDto(user);
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        List<UserDto> userDtos = users.stream().map(user -> entityToDto(user)).collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public List<UserDto> setPasswordByEncode() {
        List<User> users = userRepository.findAll();

        List<User> collect = users.stream().map(user -> {
            user.setPassword("$2a$10$C3fG1dsE8WtbuGVkqZt.WO4KTootRLApeFGagS9fvSmYiGoMfU8xy");
            userRepository.save(user);
            return user;
        }).collect(Collectors.toList());

        List<UserDto> userDto = collect.stream().map(user -> {
            return mapper.map(user, UserDto.class);
        }).collect(Collectors.toList());
        return userDto;
    }
}
