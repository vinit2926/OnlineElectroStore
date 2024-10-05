package com.electronicstore.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "id")
    private String categoryId;
    @Column(name = "category_title",length = 60)
    private String title;
    @Column(name = "category_desc",length = 400)
    private String description;
    private String coverImage;

    // cascade.ALL means if remove category then product will also remove.
    // FetchType.LAZY means when we fetch category then product will not fetch products will fetch on demand
    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

}
