package com.demo.ticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.demo.ticket.dto.AgencyCreateDTO;
import com.demo.ticket.dto.AgencyResponse;

import com.demo.ticket.entity.Agency;
import com.demo.ticket.serviceImpl.AgencyServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/agencies")
@RequiredArgsConstructor
public class AgencyController {

	private final AgencyServiceImpl agencyService;

	// POST /api/agencies (add new agency)
	@PostMapping
	public ResponseEntity<Map<String, String>> create(@Valid @RequestBody AgencyCreateDTO dto) {

		agencyService.create(Agency.builder().name(dto.getName()).contactPersonName(dto.getContactPersonName())
				.email(dto.getEmail()).phone(dto.getPhone()).build());

		Map<String, String> response = new HashMap<>();
		response.put("message", "Record created successfully");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// GET /api/agencies (all agencies)
	@GetMapping
	public List<AgencyResponse> getAll() {
		return agencyService.getAll().stream().map(AgencyResponse::fromEntity).toList();
	}

	// GET /api/agencies/{agency_id}
	@GetMapping("/{agencyId}")
	public AgencyResponse getById(@PathVariable Integer agencyId) {
		return AgencyResponse.fromEntity(agencyService.get(agencyId));
	}

	@PutMapping("/{agencyId}")
	public ResponseEntity<Map<String, String>> update(@PathVariable Integer agencyId,
			@Valid @RequestBody AgencyCreateDTO dto, org.springframework.validation.BindingResult br) {

		if (br.hasErrors()) {
			String msg = br.getFieldErrors().stream().map(err -> err.getDefaultMessage()).findFirst()
					.orElse("Validation failed");
			return ResponseEntity.badRequest().body(Map.of("message", msg));
		}

		agencyService.update(agencyId, Agency.builder().name(dto.getName())
				.contactPersonName(dto.getContactPersonName()).email(dto.getEmail()).phone(dto.getPhone()).build());

		return ResponseEntity.ok(Map.of("message", "Record updated successfully"));
	}

}
