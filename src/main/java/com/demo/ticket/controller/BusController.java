package com.demo.ticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.demo.ticket.dto.BusCreateDTO;
import com.demo.ticket.dto.BusResponse;
import com.demo.ticket.entity.Bus;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.BusRepository;
import com.demo.ticket.serviceImpl.BusServiceImpl;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/buses")
@RequiredArgsConstructor
public class BusController {

	private final BusServiceImpl busService;
	private final BusRepository busRepo;

	@GetMapping
	public List<BusResponse> getAll() {
		return busRepo.findAll().stream().map(BusResponse::fromEntity).toList();
	}

	@GetMapping("/{busId}")
	public BusResponse getById(@PathVariable Integer busId) {
		return BusResponse
				.fromEntity(busRepo.findById(busId).orElseThrow(() -> new ResourceNotFoundException("Bus not found")));
	}

	@GetMapping("/office/{officeId}")
	public List<BusResponse> getByOffice(@PathVariable Integer officeId) {
		return busService.getByOffice(officeId).stream().map(BusResponse::fromEntity).toList();
	}

	@PostMapping
	public ResponseEntity<Map<String, String>> create(@Valid @RequestBody BusCreateDTO dto) {

		busService.create(dto.getOfficeId(), Bus.builder().registrationNumber(dto.getRegistrationNumber())
				.capacity(dto.getCapacity()).type(dto.getType()).build());

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Record inserted successfully"));
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestParam("id") Integer busId, // id as query param
			@Valid @RequestBody BusCreateDTO dto, org.springframework.validation.BindingResult br) {

		if (br.hasErrors()) {
			String msg = br.getFieldErrors().stream().map(err -> err.getDefaultMessage()).findFirst()
					.orElse("Validation failed");
			// Keep the same shape as success (JSON with { "message": ... })
			return ResponseEntity.badRequest().body(Map.of("message", msg));
		}

		busService.update(busId, dto.getOfficeId(), Bus.builder().registrationNumber(dto.getRegistrationNumber())
				.capacity(dto.getCapacity()).type(dto.getType()).build());

		return ResponseEntity.ok(Map.of("message", "Record updated successfully"));
	}

}
