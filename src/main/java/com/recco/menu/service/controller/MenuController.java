package com.recco.menu.service.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recco.menu.service.model.Category;
import com.recco.menu.service.model.MenuItem;
import com.recco.menu.service.model.Preference;
import com.recco.menu.service.services.MenuService;
import com.recco.menu.service.util.TableIdDecoder;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/menu")
public class MenuController {

	private final MenuService menuService;

	@Autowired
	TableIdDecoder tableIdDecoder;
	
	private final Path imageStoragePath = Paths.get("images");

	public MenuController(MenuService menuService) {
		this.menuService = menuService;
	}

	// ✅ Extract tableId from qrToken
	private String extractTableId(String qrToken) {
	    try {
	        return tableIdDecoder.decodeTableId(qrToken);
	    } catch (Exception e) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid QR Token");
	    }
	}
	
    /** ✅ SAVE IMAGE TO FILESYSTEM */
    private String saveImage(MultipartFile image) throws IOException {
        if (!Files.exists(imageStoragePath)) {
            Files.createDirectories(imageStoragePath);  // Ensure directory exists
        }
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = imageStoragePath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

	/** ✅ GET ALL MENU ITEMS */
	@GetMapping("/all")
	public ResponseEntity<List<MenuItem>> getAllMenuItems(
			@RequestParam(value = "qrToken", required = true) String qrToken) {

		String tableId = extractTableId(qrToken);
		System.out.println("Serving menu to table: " + tableId);
		return ResponseEntity.ok(menuService.getAllMenuItems(tableId));
	}

	/** ✅ GET ITEM BY NAME */
	@GetMapping("/item")
	public ResponseEntity<MenuItem> getItemByName(@RequestParam(value = "qrToken", required = true) String qrToken,
			@RequestParam("name") String name) {
		String tableId = extractTableId(qrToken);
		System.out.println("Item request from table: " + tableId);
		return ResponseEntity.ok(menuService.getMenuItemByName(name));
	}

	/** ✅ ADD NEW MENU ITEM */
//	@PostMapping("/add")
//	public ResponseEntity<MenuItem> addMenuItem(@RequestParam(value = "qrToken", required = true) String qrToken,
//			@RequestBody MenuItem menuItem) {
//
//		String tableId = extractTableId(qrToken);
//		System.out.println("Adding item from table: " + tableId);
//		return ResponseEntity.ok(menuService.addMenuItem(menuItem));
//	}
	
	 /** ✅ ADD MENU ITEM WITH IMAGE */
//    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<MenuItem> addMenuItem(
//            @RequestParam("qrToken") String qrToken,
//            @RequestParam("name") String name,
//            @RequestParam("description") String description,
//            @RequestParam("preference") String preference,
//            @RequestParam("category") String category,
//            @RequestParam("price") Double price,
//            @RequestParam("image") MultipartFile image) throws IOException {
//
//        String tableId = extractTableId(qrToken);
//        System.out.println("Adding item from table: " + tableId);
//
//        // ✅ Save image file
//        String fileName = saveImage(image);
//
//        MenuItem menuItem = new MenuItem();
//        menuItem.setName(name);
//        menuItem.setDescription(description);
//        menuItem.setPreference(Preference.valueOf(preference.toUpperCase()));
//        menuItem.setCategory(Category.valueOf(category.toUpperCase()));
//        menuItem.setPrice(price);
//        menuItem.setImagePath(fileName); 
//
//        return ResponseEntity.ok(menuService.addMenuItem(menuItem));
//    }
	
	/** ✅ ADD MENU ITEM WITH IMAGE */
	@PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MenuItem> addMenuItem(
	        @RequestParam("menuItem") String menuItemJson,  // JSON as a String
	        @RequestParam("image") MultipartFile image) throws IOException {

	    ObjectMapper objectMapper = new ObjectMapper();
	    MenuItem menuItem = objectMapper.readValue(menuItemJson, MenuItem.class);  // Convert JSON String to Object

	    String fileName = saveImage(image);  // Save the image file
	    menuItem.setImagePath(fileName);  // Set the image path in the menu item

	    return ResponseEntity.ok(menuService.addMenuItem(menuItem));
	}

    /** ✅ SERVE IMAGES */
    @GetMapping("/image")
    public ResponseEntity<UrlResource> getImage(@RequestParam(value = "imageName", required = true) String imageName) {
        try {
            Path filePath = imageStoragePath.resolve(imageName).normalize();
            UrlResource resource = new UrlResource(filePath.toUri()); // ✅ No mismatch now

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // ✅ Change type if needed (PNG, etc.)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

	/** ✅ DELETE ITEM BY NAME */
	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteMenuItem(
//			@RequestParam(value = "qrToken", required = true) String qrToken,
			@RequestParam("name") String name) {

//		String tableId = extractTableId(qrToken);
//		System.out.println("Deleting item from table: " + tableId);
		menuService.deleteMenuItemByName(name);
		return ResponseEntity.ok("Item deleted successfully");
	}

	/** ✅ EDIT MENU ITEM */
	@PutMapping("/edit")
	public ResponseEntity<MenuItem> editMenuItem(
//			@RequestParam(value = "qrToken", required = true) String qrToken,
			@RequestParam("name") String name, @RequestBody MenuItem menuItem) {

//		String tableId = extractTableId(qrToken);
//		System.out.println("Editing item from table: " + tableId);
		return ResponseEntity.ok(menuService.updateMenuItem(name, menuItem));
	}

	/** ✅ FILTER MENU ITEMS (BY CATEGORY, PREFERENCE, OR BOTH) */
	@GetMapping
	public ResponseEntity<List<MenuItem>> getFilteredMenu(
			@RequestParam(value = "qrToken", required = true) String qrToken,
			@RequestParam(value = "category", required = false) String category,
			@RequestParam(value = "preference", required = false) String preference) {

		String tableId = extractTableId(qrToken);
		System.out.println("Filtered menu accessed from table: " + tableId);

		if (category != null && preference != null) {
			return ResponseEntity.ok(menuService.getItemsByCategoryAndPreference(
					Category.valueOf(category.toUpperCase()), Preference.valueOf(preference.toUpperCase())));
		}
		if (category != null) {
			return ResponseEntity.ok(menuService.getItemsByCategory(Category.valueOf(category.toUpperCase())));
		}
		if (preference != null) {
			return ResponseEntity.ok(menuService.getItemsByPreference(Preference.valueOf(preference.toUpperCase())));
		}
		return ResponseEntity.ok(menuService.getAllMenuItems(tableId));
	}

//    ** ✅ GET PRICE OF A MENU ITEM */
	@GetMapping("/price")
	public ResponseEntity<Double> getMenuItemPrice(
			@RequestParam(value = "qrToken", required = true) String qrToken,
			@RequestParam("name") String name) {

		String tableId = extractTableId(qrToken);
		System.out.println(
				"Fetching price for item: " + name + " from table: " + (tableId != null ? tableId : "No table"));

		// ✅ Fetch menu item by name
		MenuItem menuItem = menuService.getMenuItemByName(name);
		if (menuItem == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(menuItem.getPrice());
	}

}
