package com.svs.etracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.svs.etracker.model.Category;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {



	@Query("select c.category from Category c")
	public List<String> getOnlyCategories();

	@Query("select c.categoryId from Category c where c.category = :category")
	public int getIdForCategory(@Param("category") String category);


	public Category findByCategory(String category);



}
