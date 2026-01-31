package com.stayinn.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stayinn.dto.Villa.VillaCreateDTO;
import com.stayinn.dto.Villa.VillaDetailDTO;
import com.stayinn.dto.Villa.VillaResponseDTO;
import com.stayinn.dto.Villa.VillaSearchDTO;
import com.stayinn.dto.Villa.VillaUpdateDTO;
//import com.stayinn.dto.Villa.VillaDetailDTO;
//import com.stayinn.dto.Villa.VillaResponseDTO;
//import com.stayinn.dto.Villa.VillaSearchDTO;
//import com.stayinn.dto.Villa.VillaUpdateDTO;
import com.stayinn.entities.Villa;
//import com.stayinn.repository.VillaRepository;
import com.stayinn.repository.BookingRepository;
import com.stayinn.repository.VillaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VillaServiceImpl implements VillaService {

	private final VillaRepository villaRepository;
	private final BookingRepository bookingRepository;

	@Override
	public VillaResponseDTO createVilla(VillaCreateDTO villaCreateDTO) {
		log.info("Creating new villa: {}", villaCreateDTO.getName());

		// Check if villa name already exists
		if (villaRepository.existsByName(villaCreateDTO.getName())) {
			throw new RuntimeException("Villa with name '" + villaCreateDTO.getName() + "' already exists");
		}

		// Create villa entity
		Villa villa = new Villa();
		villa.setName(villaCreateDTO.getName());
		villa.setDescription(villaCreateDTO.getDescription());
		villa.setAddress(villaCreateDTO.getAddress());
		villa.setPricePerNight(villaCreateDTO.getPricePerNight());
		villa.setImageUrls(villaCreateDTO.getImageUrls() != null ? new ArrayList<>(villaCreateDTO.getImageUrls())
				: new ArrayList<>());

		Villa savedVilla = villaRepository.save(villa);
		log.info("Villa created successfully with ID: {}", savedVilla.getId());

		return mapToResponseDTO(savedVilla);
	}

	@Override
	@Transactional(readOnly = true)
	public VillaResponseDTO getVillaById(Long id) {
		log.info("Fetching villa with ID: {}", id);
		Villa villa = villaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Villa not found with ID: " + id));
		return mapToResponseDTO(villa);
	}

//    @Override
//    @Transactional(readOnly = true)
//    public VillaDetailDTO getVillaDetailById(Long id) {
//        return villaRepository.findVillaDetailDTO(id);
//    }

	@Override
	@Transactional(readOnly = true)
	public VillaDetailDTO getVillaDetailById(Long id) {
		log.info("Fetching detailed villa information for ID: {}", id);

		Villa villa = villaRepository.findByIdWithRatings(id)
				.orElseThrow(() -> new RuntimeException("Villa not found with ID: " + id));

		return mapToDetailDTO(villa);
	}

	@Override
	@Transactional(readOnly = true)
	public List<VillaResponseDTO> getAllVillas() {
		log.info("Fetching all villas");
		return villaRepository.findAll().stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

//    @Override
//    @Transactional(readOnly = true)
//    public List<VillaSummaryDTO> getAllVillaSummaries() {
//        log.info("Fetching all villa summaries");
//        return villaRepository.findAll().stream()
//                .map(this::mapToSummaryDTO)
//                .collect(Collectors.toList());
//    }
//    
	@Override
	public VillaResponseDTO updateVilla(Long id, VillaUpdateDTO villaUpdateDTO) {
		log.info("Updating villa with ID: {}", id);

		Villa villa = villaRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Villa not found with ID: " + id));

		// Update fields if provided
		if (villaUpdateDTO.getName() != null) {
			// Check if new name conflicts with another villa
			if (!villa.getName().equals(villaUpdateDTO.getName())
					&& villaRepository.existsByName(villaUpdateDTO.getName())) {
				throw new RuntimeException("Villa with name '" + villaUpdateDTO.getName() + "' already exists");
			}
			villa.setName(villaUpdateDTO.getName());
		}

		if (villaUpdateDTO.getDescription() != null) {
			villa.setDescription(villaUpdateDTO.getDescription());
		}

		if (villaUpdateDTO.getAddress() != null) {
			villa.setAddress(villaUpdateDTO.getAddress());
		}

		if (villaUpdateDTO.getPricePerNight() != null) {
			villa.setPricePerNight(villaUpdateDTO.getPricePerNight());
		}

		if (villaUpdateDTO.getImageUrls() != null) {
			villa.setImageUrls(new ArrayList<>(villaUpdateDTO.getImageUrls()));
		}

		Villa updatedVilla = villaRepository.save(villa);
		log.info("Villa updated successfully");

		return mapToResponseDTO(updatedVilla);
	}

	@Override
	public void deleteVilla(Long id) {
		log.info("Deleting villa with ID: {}", id);

		if (!villaRepository.existsById(id)) {
			throw new RuntimeException("Villa not found with ID: " + id);
		}

		// Check if villa has any bookings
		long bookingCount = bookingRepository.countByVillaId(id);
		if (bookingCount > 0) {
			throw new RuntimeException("Cannot delete villa with existing bookings. Please cancel all bookings first.");
		}

		villaRepository.deleteById(id);
		log.info("Villa deleted successfully");
	}

	@Override
	@Transactional(readOnly = true)
	public List<VillaResponseDTO> searchVillas(String keyword) {
		log.info("Searching villas with keyword: {}", keyword);
		return villaRepository.searchVillas(keyword).stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<VillaResponseDTO> filterVillas(VillaSearchDTO searchDTO) {
		log.info("Filtering villas with criteria: {}", searchDTO);

		List<Villa> villas = villaRepository.findVillasWithFilters(searchDTO.getName(), searchDTO.getAddress(),
				searchDTO.getMinPrice(), searchDTO.getMaxPrice());

		List<VillaResponseDTO> results = villas.stream().map(this::mapToResponseDTO).collect(Collectors.toList());

		// Apply sorting if specified
		if (searchDTO.getSortBy() != null) {
			switch (searchDTO.getSortBy().toLowerCase()) {
			case "price_asc":
				results.sort(Comparator.comparing(VillaResponseDTO::getPricePerNight));
				break;
			case "price_desc":
				results.sort(Comparator.comparing(VillaResponseDTO::getPricePerNight).reversed());
				break;
			case "rating":
				results.sort(Comparator.comparing(VillaResponseDTO::getAverageRating).reversed());
				break;
			case "name":
				results.sort(Comparator.comparing(VillaResponseDTO::getName));
				break;
			}
		}

		return results;
	}

	@Override
	@Transactional(readOnly = true)
	public List<VillaResponseDTO> getVillasByPriceRange(Double minPrice, Double maxPrice) {
		log.info("Fetching villas in price range: {} - {}", minPrice, maxPrice);
		return villaRepository.findByPricePerNightBetween(minPrice, maxPrice).stream().map(this::mapToResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<VillaResponseDTO> getVillasByAddress(String address) {
		log.info("Fetching villas by address: {}", address);
		return villaRepository.findByAddressContainingIgnoreCase(address).stream().map(this::mapToResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<VillaResponseDTO> getTopRatedVillas(int limit) {
		log.info("Fetching top {} rated villas", limit);
		return villaRepository.findTopRatedVillas().stream().limit(limit).map(this::mapToResponseDTO)
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<VillaResponseDTO> getVillasSortedByPrice(boolean ascending) {
		log.info("Fetching villas sorted by price (ascending: {})", ascending);
		List<Villa> villas = ascending ? villaRepository.findAllByOrderByPricePerNightAsc()
				: villaRepository.findAllByOrderByPricePerNightDesc();

		return villas.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
	}

	@Override
	public VillaResponseDTO addImageToVilla(Long villaId, String imageUrl) {
		log.info("Adding image to villa ID: {}", villaId);

		Villa villa = villaRepository.findById(villaId)
				.orElseThrow(() -> new RuntimeException("Villa not found with ID: " + villaId));

		if (villa.getImageUrls() == null) {
			villa.setImageUrls(new ArrayList<>());
		}

		if (villa.getImageUrls().size() >= 10) {
			throw new RuntimeException("Maximum 10 images allowed per villa");
		}

		if (!villa.getImageUrls().contains(imageUrl)) {
			villa.getImageUrls().add(imageUrl);
			Villa updatedVilla = villaRepository.save(villa);
			log.info("Image added successfully");
			return mapToResponseDTO(updatedVilla);
		}

		log.warn("Image URL already exists for villa");
		return mapToResponseDTO(villa);
	}

	@Override
	public VillaResponseDTO removeImageFromVilla(Long villaId, String imageUrl) {
		log.info("Removing image from villa ID: {}", villaId);

		Villa villa = villaRepository.findById(villaId)
				.orElseThrow(() -> new RuntimeException("Villa not found with ID: " + villaId));

		if (villa.getImageUrls() != null && villa.getImageUrls().contains(imageUrl)) {
			villa.getImageUrls().remove(imageUrl);
			Villa updatedVilla = villaRepository.save(villa);
			log.info("Image removed successfully");
			return mapToResponseDTO(updatedVilla);
		}

		log.warn("Image URL not found for villa");
		return mapToResponseDTO(villa);
	}

//    @Override
//    @Transactional(readOnly = true)
//    public VillaStatisticsDTO getVillaStatistics() {
//        log.info("Generating villa statistics");
//        
//        List<Villa> allVillas = villaRepository.findAll();
//        
//        if (allVillas.isEmpty()) {
//            return new VillaStatisticsDTO(0L, 0.0, 0.0, 0.0, 0.0, 0L, 0.0);
//        }
//        
//        long totalVillas = allVillas.size();
//        
//        double averagePrice = allVillas.stream()
//                .mapToDouble(Villa::getPricePerNight)
//                .average()
//                .orElse(0.0);
//        
//        double lowestPrice = allVillas.stream()
//                .mapToDouble(Villa::getPricePerNight)
//                .min()
//                .orElse(0.0);
//        
//        double highestPrice = allVillas.stream()
//                .mapToDouble(Villa::getPricePerNight)
//                .max()
//                .orElse(0.0);
//        
//        double overallAverageRating = allVillas.stream()
//                .mapToDouble(Villa::getAverageRating)
//                .average()
//                .orElse(0.0);
//        
//        long totalBookings = bookingRepository.count();
//        
//        // Calculate total revenue from completed bookings
//        double totalRevenue = bookingRepository.findByStatus(com.stayinn.entities.BookingStatus.COMPLETED)
//                .stream()
//                .mapToDouble(com.stayinn.entities.Booking::getTotalPrice)
//                .sum();
//        
//        return new VillaStatisticsDTO(
//                totalVillas,
//                averagePrice,
//                lowestPrice,
//                highestPrice,
//                overallAverageRating,
//                totalBookings,
//                totalRevenue
//        );
//    }

	@Override
	@Transactional(readOnly = true)
	public boolean existsByName(String name) {
		return villaRepository.existsByName(name);
	}

	@Override
	@Transactional(readOnly = true)
	public long getTotalVillaCount() {
		return villaRepository.count();
	}

	// ========== HELPER METHODS ==========

	private VillaResponseDTO mapToResponseDTO(Villa villa) {
		Double averageRating = villa.getAverageRating();
		Integer totalRatings = villa.getRatings() != null ? villa.getRatings().size() : 0;

		return new VillaResponseDTO(villa.getId(), villa.getName(), villa.getDescription(), villa.getAddress(),
				villa.getPricePerNight(), villa.getImageUrls(), averageRating, totalRatings,
				villa.getCreatedAt() != null ? villa.getCreatedAt().toString() : null,
				villa.getUpdatedAt() != null ? villa.getUpdatedAt().toString() : null);
	}

//    private VillaSummaryDTO mapToSummaryDTO(Villa villa) {
//        String primaryImage = (villa.getImageUrls() != null && !villa.getImageUrls().isEmpty()) ? 
//                villa.getImageUrls().get(0) : null;
//        
//        return new VillaSummaryDTO(
//                villa.getId(),
//                villa.getName(),
//                villa.getAddress(),
//                villa.getPricePerNight(),
//                primaryImage,
//                villa.getAverageRating(),
//                villa.getRatings() != null ? villa.getRatings().size() : 0
//        );
//    }

	private VillaDetailDTO mapToDetailDTO(Villa villa) {
		VillaDetailDTO dto = new VillaDetailDTO();
		dto.setId(villa.getId());
		dto.setName(villa.getName());
		dto.setDescription(villa.getDescription());
		dto.setAddress(villa.getAddress());
		dto.setPricePerNight(villa.getPricePerNight());
		dto.setImageUrls(villa.getImageUrls());

		// Rating statistics
		dto.setAverageRating(villa.getAverageRating());
		dto.setTotalRatings(villa.getRatings() != null ? villa.getRatings().size() : 0);

		if (villa.getRatings() != null) {
			dto.setFiveStarCount((int) villa.getRatings().stream().filter(r -> r.getScore() == 5).count());
			dto.setFourStarCount((int) villa.getRatings().stream().filter(r -> r.getScore() == 4).count());
			dto.setThreeStarCount((int) villa.getRatings().stream().filter(r -> r.getScore() == 3).count());
			dto.setTwoStarCount((int) villa.getRatings().stream().filter(r -> r.getScore() == 2).count());
			dto.setOneStarCount((int) villa.getRatings().stream().filter(r -> r.getScore() == 1).count());
		}

		// Booking statistics
		dto.setTotalBookings(bookingRepository.countByVillaId(villa.getId()));
		dto.setConfirmedBookings(bookingRepository
				.findByVillaIdAndStatus(villa.getId(), com.stayinn.entities.BookingStatus.CONFIRMED).size() + 0L);

		dto.setIsAvailable(true); // Can be enhanced with real-time availability check

		dto.setCreatedAt(villa.getCreatedAt());
		dto.setUpdatedAt(villa.getUpdatedAt());

		return dto;
	}
}