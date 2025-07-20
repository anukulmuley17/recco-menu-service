package com.recco.menu.service.services;

import java.util.List;

import com.recco.menu.service.model.Category;
import com.recco.menu.service.model.MenuItem;
import com.recco.menu.service.model.Preference;

public interface AdminMenuService {
	    MenuItem getMenuItemByName(String name);
	    List<MenuItem> searchMenuItemsByName(String name);
	    List<MenuItem> getItemsByCategory(Category category);  // ✅ Now accepts Category Enum
	    List<MenuItem> getItemsByPreference(Preference preference);  // ✅ Now accepts Preference Enum
	    List<MenuItem> getItemsByCategoryAndPreference(Category category, Preference preference);  // ✅ Combo Filter
	    MenuItem addMenuItem(MenuItem menuItem);
	    MenuItem updateMenuItem(String name, MenuItem menuItem);
	    void deleteMenuItemByName(String name);
		List<MenuItem> getAllMenuItems();
}
