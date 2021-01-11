package com.reconsale.barkom.cms.repositories;

import com.reconsale.barkom.cms.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query(value = "select r from Recipe r where r.category = :c")
    List<Recipe> findAllByCategory(String c);

    @Query(value = "select r from Recipe r where lower(r.title) like lower(concat('%', :search, '%')) and r.category =:c")
    List<Recipe> findAllByCategoryAndTitle(String c, @Param("search") String title);
}
