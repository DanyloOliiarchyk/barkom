package com.reconsale.barkom.cms.services;

import com.reconsale.barkom.cms.models.Message;
import com.reconsale.barkom.cms.models.Recipe;
import com.reconsale.barkom.cms.repositories.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

    public List<Recipe> getAll(String category, String filter) {
        if (filter == null || filter.isEmpty()) {
            return recipeRepository.findAllByCategory(category);
        } else {
            return recipeRepository.findAllByCategoryAndTitle(category, filter);
        }
    }

    public List<Recipe> getAll() {
        return recipeRepository.findAll();
    }

    public void save(Recipe recipe) {
        recipeRepository.save(recipe);
    }
}
