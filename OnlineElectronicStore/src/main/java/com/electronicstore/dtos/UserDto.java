package com.electronicstore.dtos;

import com.electronicstore.validate.ImageNameValid;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private String userId;
    @Size(min = 3,max = 15,message = "Invalid name...")
    private String name;

//    @Email(message = "Invalid user email...")
    @NotBlank(message = "Email is blank...")
    @Pattern(regexp = "^[a-z0-9][-a-z0-9._]+@([-a-z0-9]+\\.)+[a-z]{2,5}$",message = "Email is invalid...")
    private String email;

    @NotBlank(message = "Password is required...")
    private String password;

    @Size(min=4,max = 6,message = "Invalid gender...")
    private String gender;

    @NotBlank(message = "Invalid about...")
    private String about;

    @ImageNameValid
    private String imageName;

    private Set<RoleDto> roles = new HashSet<>();


}
