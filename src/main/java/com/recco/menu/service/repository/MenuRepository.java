package com.recco.menu.service.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.recco.menu.service.model.MenuItem;
import com.recco.menu.service.model.Preference;
import com.recco.menu.service.model.Category;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByPreference(Preference preference);
    List<MenuItem> findByCategory(Category category);
    List<MenuItem> findByCategoryAndPreference(Category category, Preference preference);
    Optional<MenuItem> findByName(String name); // Find item by name
    void deleteByName(String name); // Delete item by name
    List<MenuItem> findByNameContainingIgnoreCase(String name);

	
}

