package com.demo.ticket.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.demo.ticket.dto.ReviewCreateDTO;
import com.demo.ticket.dto.ReviewResponse;
import com.demo.ticket.entity.Review;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.ReviewRepository;
import com.demo.ticket.serviceImpl.ReviewServiceImpl;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewServiceImpl reviewService;
	private final ReviewRepository reviewRepo;

	@PostMapping("/api/reviews")
	public ResponseEntity<java.util.Map<String, String>> create(@Valid @RequestBody ReviewCreateDTO dto,
			org.springframework.validation.BindingResult br) {

		if (br.hasErrors()) {
			String msg = br.getFieldErrors().stream().map(e -> e.getField() + ": " + e.getDefaultMessage()).findFirst()
					.orElse("Validation failed");
			java.util.Map<String, String> err = new java.util.LinkedHashMap<>();
			err.put("message", msg);
			return ResponseEntity.badRequest().body(err);
		}

		if (dto.getTripId() == null)
			return bad("tripId is required");
		if (dto.getCustomerId() == null)
			return bad("customerId is required");
		if (dto.getRouteId() == null)
			return bad("routeId is required");

		// Optional: one-liner log to confirm values arriving
		org.slf4j.LoggerFactory.getLogger(getClass()).info(
				"Review create -> tripId={}, customerId={}, routeId={}, rating={}, date={}", dto.getTripId(),
				dto.getCustomerId(), dto.getRouteId(), dto.getRating(), dto.getReviewDate());

		reviewService.create(dto.getTripId(), dto.getCustomerId(), dto.getRouteId(),
				Review.builder().rating(dto.getRating()).comment(dto.getComment()).build(), dto.getReviewDate());

		java.util.Map<String, String> ok = new java.util.LinkedHashMap<>();
		ok.put("message", "Record inserted successfully");
		return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(ok);
	}

	private ResponseEntity<java.util.Map<String, String>> bad(String msg) {
		java.util.Map<String, String> m = new java.util.LinkedHashMap<>();
		m.put("message", msg);
		return ResponseEntity.badRequest().body(m);
	}

	// GET ALL --------------------------------------------------
	@GetMapping
	public List<ReviewResponse> getAll() {
		return reviewRepo.findAll().stream().map(ReviewResponse::fromEntity).toList();
	}

	// GET BY ID --------------------------------------------------
	@GetMapping("/{reviewId}")
	public ReviewResponse getById(@PathVariable Integer reviewId) {
		return reviewRepo.findById(reviewId).map(ReviewResponse::fromEntity)
				.orElseThrow(() -> new ResourceNotFoundException("Review not found"));
	}

	// GET BY TRIP --------------------------------------------------
	@GetMapping("/tripid/{tripId}")
	public List<ReviewResponse> byTrip(@PathVariable Integer tripId) {
		return reviewService.byTrip(tripId).stream().map(ReviewResponse::fromEntity).toList();
	}

	// GET BY CUSTOMER --------------------------------------------------
	@GetMapping("/customerid/{customerId}")
	public List<ReviewResponse> byCustomer(@PathVariable Integer customerId) {
		return reviewService.byCustomer(customerId).stream().map(ReviewResponse::fromEntity).toList();
	}

	// NEW — GET BY OFFICE --------------------------------------------------
	@GetMapping("/office/{officeId}")
	public List<ReviewResponse> byOffice(@PathVariable Integer officeId) {
		return reviewService.byOffice(officeId).stream().map(ReviewResponse::fromEntity).toList();
	}

	// NEW — GET BY AGENCY --------------------------------------------------
	@GetMapping("/agency/{agencyId}")
	public List<ReviewResponse> byAgency(@PathVariable Integer agencyId) {
		return reviewService.byAgency(agencyId).stream().map(ReviewResponse::fromEntity).toList();
	}

	// NEW — GET BY DRIVER --------------------------------------------------
	@GetMapping("/driver/{driverId}")
	public List<ReviewResponse> byDriver(@PathVariable Integer driverId) {
		return reviewService.byDriver(driverId).stream().map(ReviewResponse::fromEntity).toList();
	}

	// NEW — UPDATE REVIEW --------------------------------------------------
//    @PutMapping
//    public ReviewResponse update(@Valid @RequestBody ReviewUpdateDTO dto) {
//        Review updated = reviewService.update(dto.getId(), dto.getRating(), dto.getComment());
//        return ReviewResponse.fromEntity(updated);
//    }
	@PutMapping
	public ResponseEntity<Map<String, String>> update(@RequestParam Integer reviewId,
			@Valid @RequestBody ReviewCreateDTO dto, org.springframework.validation.BindingResult br) {

		// If you want to surface bean-validation errors as the same message-only JSON
		if (br.hasErrors()) {
			String msg = br.getFieldErrors().stream().map(err -> err.getDefaultMessage()).findFirst()
					.orElse("Validation failed");

			// Java 8 compatible response body
			java.util.Map<String, String> errBody = new java.util.LinkedHashMap<>();
			errBody.put("message", msg);
			return ResponseEntity.badRequest().body(errBody);
		}

		// Perform update (service can validate rating range, ownership, existence,
		// etc.)
		reviewService.update(reviewId, dto.getTripId(), // can be same or new
				dto.getCustomerId(), // can be same or new
				dto.getRating(), // 1..5 validated in service
				dto.getComment() // optional
		);

		// Return only a message
		java.util.Map<String, String> body = new java.util.LinkedHashMap<>();
		body.put("message", "Record updated successfully");
		return ResponseEntity.ok(body);
	}

	// DTO for PUT
	@Data
	public static class ReviewUpdateDTO {
		private Integer id;
		private Integer rating;
		private String comment;
	}

	// NO DELETE (per your sir's instruction)
}
