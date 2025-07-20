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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.recco.menu.service.model.Category;
import com.recco.menu.service.model.MenuItem;
import com.recco.menu.service.model.Preference;
import com.recco.menu.service.services.MenuService;
import com.recco.menu.service.util.TableIdDecoder;

@CrossOrigin("http://localhost:4200")
@RestController
@RequestMapping("/api/menu")
public class UserMenuController {
	private final MenuService menuService;

	@Autowired
	TableIdDecoder tableIdDecoder;
	
	private final Path imageStoragePath = Paths.get("images");

	public UserMenuController(MenuService menuService) {
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
		return ResponseEntity.ok(menuService.getAllMenuItems());
	}

	/** ✅ GET ITEM BY NAME */
	@GetMapping("/item")
	public ResponseEntity<MenuItem> getItemByName(@RequestParam(value = "qrToken", required = true) String qrToken,
			@RequestParam("name") String name) {
		String tableId = extractTableId(qrToken);
		System.out.println("Item request from table: " + tableId);
		return ResponseEntity.ok(menuService.getMenuItemByName(name));
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
		return ResponseEntity.ok(menuService.getAllMenuItems());
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
