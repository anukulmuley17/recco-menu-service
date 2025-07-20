package com.recco.menu.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recco.menu.service.exception.MenuItemNotFoundException;
import com.recco.menu.service.model.Category;
import com.recco.menu.service.model.MenuItem;
import com.recco.menu.service.model.Preference;
import com.recco.menu.service.services.AdminMenuService;

@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    private final MenuRepository menuRepository;
    
    @Autowired
    public AdminMenuServiceImpl(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
       
        return menuRepository.findAll();
    }

    @Override
    public MenuItem getMenuItemByName(String name) {
        return menuRepository.findByName(name)
                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found: " + name));
    }

    @Override
    public List<MenuItem> getItemsByPreference(Preference preference) {
        try {
            return menuRepository.findByPreference(preference);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid preference type: " + preference);
        }
    }

    @Override
    public List<MenuItem> getItemsByCategory(Category category) {
        try {
            return menuRepository.findByCategory(category);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category type: " + category);
        }
    }
    
    @Override
    public List<MenuItem> getItemsByCategoryAndPreference(Category category, Preference preference) {
        return menuRepository.findByCategoryAndPreference(category, preference);
    }
    
//    @Override
//    public MenuItem addMenuItem(MenuItem menuItem) {
//        Optional<MenuItem> existingItem = menuRepository.findByName(menuItem.getName());
//        if (existingItem.isPresent()) {
//            throw new IllegalArgumentException("Menu item already exists with name: " + menuItem.getName());
//        }
//        return menuRepository.save(menuItem);
//    }
    
    @Override
    public MenuItem addMenuItem(MenuItem menuItem) {
        Optional<MenuItem> existingItem = menuRepository.findByName(menuItem.getName());
        if (existingItem.isPresent()) {
            throw new IllegalArgumentException("Menu item already exists: " + menuItem.getName());
        }
        return menuRepository.save(menuItem);
    }

//    @Override
//    public MenuItem getMenuItemByName(String name) {
//        MenuItem item = menuRepository.findByName(name)
//                .orElseThrow(() -> new MenuItemNotFoundException("Menu item not found: " + name));
//
//        // ✅ Append image URL
//        String imageUrl = "http://localhost:8082/api/menu/image/" + item.getImagePath();
//        item.setImagePath(imageUrl);
//        return item;
//    }


    @Override
    public MenuItem updateMenuItem(String name, MenuItem menuItem) {
        MenuItem existingItem = getMenuItemByName(name);
        existingItem.setDescription(menuItem.getDescription());
        existingItem.setPreference(menuItem.getPreference());
        existingItem.setCategory(menuItem.getCategory());
        existingItem.setPrice(menuItem.getPrice());
        return menuRepository.save(existingItem);
    }

    @Override
    public void deleteMenuItemByName(String name) {
        MenuItem menuItem = getMenuItemByName(name);
        menuRepository.delete(menuItem);
    }

    @Override
    public List<MenuItem> searchMenuItemsByName(String name) {
        List<MenuItem> items = menuRepository.findByNameContainingIgnoreCase(name);

        if (items.isEmpty()) {
            throw new MenuItemNotFoundException("No menu items found for search: " + name);
        }

        // Update image URL for each item
        for (MenuItem item : items) {
            String imageUrl = "http://localhost:8082/api/menu/image/" + item.getImagePath();
            item.setImagePath(imageUrl);
        }

        return items;
    }

}
