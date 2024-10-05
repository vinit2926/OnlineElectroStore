package com.electronicstore.services.impl;

import com.electronicstore.dtos.CategoryDto;
import com.electronicstore.dtos.PageableResponse;
import com.electronicstore.entities.Category;
import com.electronicstore.exceptions.ResourceNotFoundException;
import com.electronicstore.helper.Helper;
import com.electronicstore.repositories.CategoryRepository;
import com.electronicstore.services.CategoryService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${categories.profile.image.path}")
    private String categoryImagePath;

    @Autowired
    private ModelMapper mapper;

    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        String randomId = UUID.randomUUID().toString();
        //we can put id in entity or DTO
        categoryDto.setCategoryId(randomId);
        Category category = mapper.map(categoryDto, Category.class);
        Category savedCategory = categoryRepository.save(category);
        return mapper.map(savedCategory,CategoryDto.class);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found..."));

        //update category details
        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCoverImage(categoryDto.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);
        return mapper.map(updatedCategory,CategoryDto.class);
    }

    @Override
    public void delete(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found..."));
        //delete product image
        // images/user/abc.png
        String fullPath = categoryImagePath+category.getCoverImage();
        try
        {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        }
        catch (NoSuchFileException e){
//            logger.info("Category image not found in folder");
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAll(int pageNumber,int pageSize,String sortBy,String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        //pageNumber default starts from 0
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
        Page<Category> page = categoryRepository.findAll(pageable);

        PageableResponse<CategoryDto> pageablersponse = Helper.getPageableResponse(page, CategoryDto.class);
        return pageablersponse;
    }

    @Override
    public CategoryDto get(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found..."));
        return mapper.map(category,CategoryDto.class);
    }


}
