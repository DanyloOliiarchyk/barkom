package com.reconsale.barkom.cms.menu;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Category {
    private static int count = 0;
    private Integer id;
    private String name;
    private List<String> products;
    private List<Category> subCategories;

    public Category() {
        id = ++count;
        this.products = new ArrayList<>();
        this.subCategories = new ArrayList<>();
    }

    public void addProduct(String productName) {
        this.products.add(productName);
    }

    public void addSubCategory(Category category) {
        this.subCategories.add(category);
    }

    public boolean isEmpty() {
        if (this.subCategories.isEmpty() && this.products.isEmpty() && this.name == null) return true;
        return false;
    }
}
