package com.demo.ticket.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;

import com.demo.ticket.dto.TripCreateDTO;

import com.demo.ticket.dto.TripResponse;

import com.demo.ticket.entity.Trip;

import com.demo.ticket.repository.TripRepository;

import com.demo.ticket.serviceImpl.TripServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController

@RequestMapping("/api/trips")

@RequiredArgsConstructor

public class TripController {

	private final TripServiceImpl tripService;

	private final TripRepository tripRepo;

	// CSV: GET /api/trips

	@GetMapping

	public List<TripResponse> getAll() {

		return tripRepo.findAll().stream().map(TripResponse::fromEntity).toList();

	}

	// CSV: GET /api/trips/{trip_id}

	@GetMapping("/{tripId}")

	public TripResponse getById(@PathVariable Integer tripId) {

		return TripResponse.fromEntity(tripService.get(tripId));

	}

	// CSV: GET /api/trips/from_city/{from_city}

	@GetMapping("/from_city/{fromCity}")

	public List<TripResponse> fromCity(@PathVariable String fromCity) {

		return tripService.fromCity(fromCity).stream().map(TripResponse::fromEntity).toList();

	}

	// CSV: GET /api/trips/to_city/{to_city}

	@GetMapping("/to_city/{toCity}")

	public List<TripResponse> toCity(@PathVariable String toCity) {

		return tripService.toCity(toCity).stream().map(TripResponse::fromEntity).toList();

	}

	// CSV: GET /api/trips/bus_type/{type}

	@GetMapping("/bus_type/{type}")

	public List<TripResponse> byBusType(@PathVariable String type) {

		return tripService.byBusType(type).stream().map(TripResponse::fromEntity).toList();

	}

	// CSV: GET /api/trips/trip_date/{trip_date}

	@GetMapping("/trip_date/{tripDate}")

	public List<TripResponse> byTripDate(@PathVariable String tripDate) {

		return tripRepo.findAll().stream()

				.filter(t -> t.getTripDate() != null && t.getTripDate().toLocalDate().toString().equals(tripDate))

				.map(TripResponse::fromEntity)

				.toList();

	}

	// ---------------------------------------------------------

	// ✅ NEW: CSV combined filters (no DELETE per your instruction)

	// ---------------------------------------------------------

	// CSV: GET /api/trips/{from_city}/{to_city}/{trip_date}

	@GetMapping("/{fromCity}/{toCity}/{tripDate}")

	public List<TripResponse> byFromToDate(@PathVariable("fromCity") String fromCity,

			@PathVariable("toCity") String toCity,

			@PathVariable("tripDate") String tripDate) {

		LocalDate target = LocalDate.parse(tripDate); // expecting yyyy-MM-dd like in insert.sql

		return tripRepo.findAll().stream()

				.filter(t -> t.getRoute() != null && t.getRoute().getFromCity() != null
						&& t.getRoute().getToCity() != null && t.getTripDate() != null)

				.filter(t -> fromCity.equalsIgnoreCase(t.getRoute().getFromCity()))

				.filter(t -> toCity.equalsIgnoreCase(t.getRoute().getToCity()))

				.filter(t -> t.getTripDate().toLocalDate().isEqual(target))

				.map(TripResponse::fromEntity)

				.collect(Collectors.toList());

	}

	@GetMapping("/{fromCity}/{toCity}/{tripDate}/{busType}")
	public List<TripResponse> byFromToDateType(@PathVariable String fromCity, @PathVariable String toCity,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS") LocalDateTime tripDate,
			@PathVariable String busType) {
		LocalDate date = tripDate.toLocalDate();
		return tripService.search(fromCity, toCity, date, busType).stream().map(TripResponse::fromEntity).toList();
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> create(@Valid @RequestBody TripCreateDTO dto) {

		tripService.create(Trip.builder().departureTime(dto.getDepartureTime()).arrivalTime(dto.getArrivalTime())
				.availableSeats(dto.getAvailableSeats()).fare(dto.getFare()).tripDate(dto.getTripDate()).build(),
				dto.getRouteId(), dto.getBusId(), dto.getDriver1Id(), dto.getDriver2Id(), dto.getBoardingAddressId(),
				dto.getDroppingAddressId());

		// If you're on Java 9+, you can do: return
		// ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Record
		// inserted successfully"));
		Map<String, String> body = new java.util.LinkedHashMap<>();
		body.put("message", "Record inserted successfully");
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}

	@PutMapping
	public ResponseEntity<Map<String, String>> updateTrip(@RequestParam("id") Integer tripId, // id as query param
			@Valid @RequestBody TripCreateDTO dto) {

		tripService.update(tripId, dto.getRouteId(), dto.getBusId(), dto.getDriver1Id(), dto.getDriver2Id(),
				dto.getBoardingAddressId(), dto.getDroppingAddressId(), dto.getDepartureTime(), dto.getArrivalTime(),
				dto.getAvailableSeats(), dto.getFare(), dto.getTripDate());

		Map<String, String> body = new java.util.LinkedHashMap<>();
		body.put("message", "Record updated successfully");
		return ResponseEntity.ok(body);
	}

}
