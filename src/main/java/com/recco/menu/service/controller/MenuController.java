package com.recco.menu.service.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.recco.menu.service.model.MenuItem;
import com.recco.menu.service.model.Preference;
import com.recco.menu.service.model.Category;
import com.recco.menu.service.services.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
   
    public MenuController(MenuService menuService) {
		this.menuService = menuService;
	}
    
    @GetMapping("/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems(@RequestParam(name = "tableId", required = false) String tableId) {
        if (tableId != null) {
            System.out.println("Menu accessed from table: " + tableId);
        }
        List<MenuItem> menuItems = menuService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }
    
	// ✅ Get All Items or Filter by Category & Preference
    @GetMapping
    public List<MenuItem> getFilteredMenu(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "preference", required = false) String preference) {

        if (category != null && preference != null) {
            return menuService.getItemsByCategoryAndPreference(
                    Category.valueOf(category.toUpperCase()), 
                    Preference.valueOf(preference.toUpperCase()));
        }
        if (category != null) {
            return menuService.getItemsByCategory(Category.valueOf(category.toUpperCase()));
        }
        if (preference != null) {
            return menuService.getItemsByPreference(Preference.valueOf(preference.toUpperCase()));
        }
        return menuService.getAllMenuItems();
    }

    // ✅ Get a Menu Item by Name
    @GetMapping("/item")
    public ResponseEntity<MenuItem> getItem(@RequestParam("name") String name) {
        return ResponseEntity.ok(menuService.getMenuItemByName(name));
    }

    // ✅ Add a New Menu Item
    @PostMapping("/add")
    public ResponseEntity<MenuItem> addMenuItem(@RequestBody MenuItem menuItem) {
        MenuItem savedItem = menuService.addMenuItem(menuItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    // ✅ Update a Menu Item by Name
    @PutMapping("/edit")
    public MenuItem updateItem(@RequestParam("name") String name, @RequestBody MenuItem menuItem) {
        return menuService.updateMenuItem(name, menuItem);
    }


    // ✅ Delete a Menu Item by Name
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteItem(@RequestParam("name") String name) {
        menuService.deleteMenuItem(name);
        return ResponseEntity.ok("Menu item deleted successfully");
    }
    
 // ✅ Fetch Price of an Item by Name
    @GetMapping("/price")
    public ResponseEntity<Double> getPrice(@RequestParam("name") String name) {
        MenuItem item = menuService.getMenuItemByName(name);
        if (item != null && item.getPrice() != null) {
            return ResponseEntity.ok(item.getPrice());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
