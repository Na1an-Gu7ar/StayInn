package com.stayinn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
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

import com.stayinn.dto.Villa.VillaCreateDTO;
import com.stayinn.dto.Villa.VillaDetailDTO;
import com.stayinn.dto.Villa.VillaResponseDTO;
import com.stayinn.dto.Villa.VillaSearchDTO;
import com.stayinn.dto.Villa.VillaUpdateDTO;
import com.stayinn.service.VillaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/villas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VillaController {
    
    private final VillaService villaService;
    
    // ========== PUBLIC ENDPOINTS ==========
    
    /**
     * Get all villas
     * GET /api/villas
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVillas() {
        List<VillaResponseDTO> villas = villaService.getAllVillas();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", villas.size());
        response.put("data", villas);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all villa summaries (for listing page)
     * GET /api/villas/summaries
     */
//    @GetMapping("/summaries")
//    public ResponseEntity<Map<String, Object>> getAllVillaSummaries() {
//        List<VillaSummaryDTO> summaries = villaService.getAllVillaSummaries();
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("count", summaries.size());
//        response.put("data", summaries);
//        return ResponseEntity.ok(response);
//    }
    
    /**
     * Get villa by ID
     * GET /api/villas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getVillaById(@PathVariable Long id) {
        try {
            VillaResponseDTO villa = villaService.getVillaById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", villa);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Get detailed villa information
     * GET /api/villas/{id}/details
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getVillaDetails(@PathVariable Long id) {
        try {
            VillaDetailDTO villaDetail = villaService.getVillaDetailById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", villaDetail);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    
    /**
     * Search villas by keyword
     * GET /api/villas/search?keyword=beach
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchVillas(@RequestParam String keyword) {
        List<VillaResponseDTO> villas = villaService.searchVillas(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", villas.size());
        response.put("data", villas);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Filter villas with multiple criteria
     * POST /api/villas/filter
     */
    @PostMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterVillas(@Valid @RequestBody VillaSearchDTO searchDTO) {
        List<VillaResponseDTO> villas = villaService.filterVillas(searchDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", villas.size());
        response.put("data", villas);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get villas by price range
     * GET /api/villas/price-range?minPrice=1000&maxPrice=5000
     */
    @GetMapping("/price-range")
    public ResponseEntity<Map<String, Object>> getVillasByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<VillaResponseDTO> villas = villaService.getVillasByPriceRange(minPrice, maxPrice);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", villas.size());
        response.put("data", villas);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get villas by location
     * GET /api/villas/location?address=Goa
     */
    @GetMapping("/location")
    public ResponseEntity<Map<String, Object>> getVillasByLocation(@RequestParam String address) {
        List<VillaResponseDTO> villas = villaService.getVillasByAddress(address);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", villas.size());
        response.put("data", villas);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get top-rated villas
     * GET /api/villas/top-rated?limit=5
     */
    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> getTopRatedVillas(
            @RequestParam(defaultValue = "10") int limit) {
        List<VillaResponseDTO> villas = villaService.getTopRatedVillas(limit);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", villas.size());
        response.put("data", villas);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get villas sorted by price
     * GET /api/villas/sort-by-price?ascending=true
     */
    @GetMapping("/sort-by-price")
    public ResponseEntity<Map<String, Object>> getVillasSortedByPrice(
            @RequestParam(defaultValue = "true") boolean ascending) {
        List<VillaResponseDTO> villas = villaService.getVillasSortedByPrice(ascending);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", villas.size());
        response.put("data", villas);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get villa statistics (Admin only)
     * GET /api/villas/statistics
     */
//    @GetMapping("/statistics")
//    public ResponseEntity<Map<String, Object>> getVillaStatistics() {
//        VillaStatisticsDTO statistics = villaService.getVillaStatistics();
//        Map<String, Object> response = new HashMap<>();
//        response.put("success", true);
//        response.put("data", statistics);
//        return ResponseEntity.ok(response);
//    }
    
    /**
     * Check if villa name exists
     * GET /api/villas/check-name?name=Beach Paradise
     */
    @GetMapping("/check-name")
    public ResponseEntity<Map<String, Object>> checkVillaName(@RequestParam String name) {
        boolean exists = villaService.existsByName(name);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("message", exists ? "Villa name already exists" : "Villa name available");
        return ResponseEntity.ok(response);
    }
    
    // ========== ADMIN ENDPOINTS ==========
    
    /**
     * Create a new villa (Admin only)
     * POST /api/villas
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createVilla(@Valid @RequestBody VillaCreateDTO villaCreateDTO) {
        try {
            VillaResponseDTO villa = villaService.createVilla(villaCreateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Villa created successfully");
            response.put("data", villa);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Update villa (Admin only)
     * PUT /api/villas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateVilla(
            @PathVariable Long id,
            @Valid @RequestBody VillaUpdateDTO villaUpdateDTO) {
        try {
            VillaResponseDTO villa = villaService.updateVilla(id, villaUpdateDTO);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Villa updated successfully");
            response.put("data", villa);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Delete villa (Admin only)
     * DELETE /api/villas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteVilla(@PathVariable Long id) {
        try {
            villaService.deleteVilla(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Villa deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Add image to villa (Admin only)
     * POST /api/villas/{id}/images
     */
    @PostMapping("/{id}/images")
    public ResponseEntity<Map<String, Object>> addImageToVilla(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String imageUrl = request.get("imageUrl");
            if (imageUrl == null || imageUrl.isBlank()) {
                throw new RuntimeException("Image URL is required");
            }
            
            VillaResponseDTO villa = villaService.addImageToVilla(id, imageUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image added successfully");
            response.put("data", villa);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Remove image from villa (Admin only)
     * DELETE /api/villas/{id}/images
     */
    @DeleteMapping("/{id}/images")
    public ResponseEntity<Map<String, Object>> removeImageFromVilla(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String imageUrl = request.get("imageUrl");
            if (imageUrl == null || imageUrl.isBlank()) {
                throw new RuntimeException("Image URL is required");
            }
            
            VillaResponseDTO villa = villaService.removeImageFromVilla(id, imageUrl);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Image removed successfully");
            response.put("data", villa);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}