package com.electronicstore.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
    private String categoryId;
    @NotBlank
//    @Min(value = 5,message = "Title must be of min 5 characters")
    @Size(min = 4,message = "Title must be min of 4 character...")
    private String title;
    @NotBlank(message = "Description required")
    private String description;
    private String coverImage;
}
