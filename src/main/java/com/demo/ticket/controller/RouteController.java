package com.demo.ticket.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.demo.ticket.dto.RouteCreateDTO;
import com.demo.ticket.dto.RouteResponse;
import com.demo.ticket.entity.Route;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.RouteRepository;
import com.demo.ticket.serviceImpl.RouteServiceImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

	private final RouteServiceImpl routeService;
	private final RouteRepository routeRepo;

	@PostMapping
	public ResponseEntity<Map<String, String>> create(@Valid @RequestBody RouteCreateDTO dto) {
		routeService.create(Route.builder().fromCity(dto.getFromCity()).toCity(dto.getToCity())
				.breakPoints(dto.getBreakPoints()).duration(dto.getDuration()).build());

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Record inserted successfully"));
	}

	// ---------------------------------------------------------
	// GET /api/routes (All)
	// ---------------------------------------------------------
	@GetMapping
	public List<RouteResponse> getAll() {
		return routeService.getAll().stream().map(RouteResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// GET /api/routes/{routeId} (Id - digits only to avoid clash)
	// ---------------------------------------------------------
	@GetMapping("/{routeId:\\d+}")
	public RouteResponse getById(@PathVariable Integer routeId) {
		return RouteResponse.fromEntity(routeService.get(routeId));
	}

	// ---------------------------------------------------------
	// GET /api/routes/fromcity/{fromCity}
	// ---------------------------------------------------------
	@GetMapping("/fromcity/{fromCity}")
	public List<RouteResponse> fromCity(@PathVariable String fromCity) {
		return routeService.getFrom(fromCity).stream().map(RouteResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// GET /api/routes/tocity/{toCity}
	// ---------------------------------------------------------
	@GetMapping("/tocity/{toCity}")
	public List<RouteResponse> toCity(@PathVariable String toCity) {
		return routeService.getTo(toCity).stream().map(RouteResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// ✅ CSV exact path: GET /api/routes/{from_city}/{to_city}
	// (kept after the numeric id mapping so it doesn't clash)
	// ---------------------------------------------------------
	@GetMapping("/{fromCity}/{toCity}")
	public RouteResponse byFromTo(@PathVariable String fromCity, @PathVariable String toCity) {
		return Optional.ofNullable(routeRepo.findByFromCityAndToCity(fromCity, toCity)).map(RouteResponse::fromEntity)
				.orElseThrow(() -> new ResourceNotFoundException("Route not found"));
	}

	@PutMapping
	public ResponseEntity<Map<String, String>> update(@Valid @RequestBody RouteUpdateDTO dto) {
		if (dto.getId() == null) {
			// You already have a GlobalExceptionHandler mapping this;
			// if not, this will fall back to 500—better to return 400 here:
			return ResponseEntity.badRequest().body(Map.of("message", "Route ID is required"));
		}

		// Load existing route
		Route existing = routeService.get(dto.getId()); // throws ResourceNotFoundException if not found

		// Update only provided fields
		if (dto.getFromCity() != null)
			existing.setFromCity(dto.getFromCity());
		if (dto.getToCity() != null)
			existing.setToCity(dto.getToCity());
		if (dto.getBreakPoints() != null)
			existing.setBreakPoints(dto.getBreakPoints());
		if (dto.getDuration() != null)
			existing.setDuration(dto.getDuration());

		routeRepo.save(existing);

		return ResponseEntity.ok(Map.of("message", "Record updated successfully"));
	}

	// DTO used for updating a route
	@Data
	public static class RouteUpdateDTO {
		private Integer id; // required
		private String fromCity; // optional
		private String toCity; // optional
		private Integer breakPoints; // optional
		private Integer duration; // optional
	}
}
