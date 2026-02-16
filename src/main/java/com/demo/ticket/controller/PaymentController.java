package com.demo.ticket.controller;

import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.dto.PaymentCreateDTO;
import com.demo.ticket.dto.PaymentResponse;
import com.demo.ticket.entity.Payment;
import com.demo.ticket.entity.PaymentStatus;
import com.demo.ticket.repository.PaymentRepository;
import com.demo.ticket.serviceImpl.PaymentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentServiceImpl paymentService;
	private final PaymentRepository paymentRepo;

	@PostMapping
	public ResponseEntity<Map<String, String>> create(@Valid @RequestBody PaymentCreateDTO dto) {

		// Map incoming String to enum PaymentStatus (SUCCESS / FAILED)
		PaymentStatus statusEnum = null;
		if (dto.getPaymentStatus() != null) {
			String s = dto.getPaymentStatus().trim().toLowerCase();
			if (s.startsWith("s")) {
				statusEnum = PaymentStatus.SUCCESS;
			} else if (s.startsWith("f")) {
				statusEnum = PaymentStatus.FAILED;
			} else {
				// Optional: reject invalid status with 400, keeping message-only shape
				return ResponseEntity.badRequest().body(new java.util.LinkedHashMap<String, String>() {
					{
						put("message", "Invalid paymentStatus. Allowed values: SUCCESS or FAILED");
					}
				});
			}
		}

		Payment toSave = Payment.builder().amount(dto.getAmount()).paymentDate(dto.getPaymentDate())
				.paymentStatus(statusEnum) // enum, not String
				.build();

		paymentService.create(dto.getBookingId(), dto.getCustomerId(), toSave);

		// Return only a message
		return ResponseEntity.status(HttpStatus.CREATED).body(new java.util.LinkedHashMap<String, String>() {
			{
				put("message", "Record inserted successfully");
			}
		});
	}

	@GetMapping
	public List<PaymentResponse> getAll() {
		return paymentRepo.findAll().stream().map(PaymentResponse::fromEntity).toList();
	}

	// (Optional: constrain to digits to avoid any future ambiguity)
	@GetMapping("/{paymentId:\\d+}")
	public PaymentResponse getById(@PathVariable Integer paymentId) {
		return paymentRepo.findById(paymentId).map(PaymentResponse::fromEntity)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
	}

	@GetMapping("/customer/{customerId}")
	public List<PaymentResponse> byCustomer(@PathVariable Integer customerId) {
		return paymentService.byCustomer(customerId).stream().map(PaymentResponse::fromEntity).toList();
	}

	@GetMapping("/booking/{bookingId}")
	public List<PaymentResponse> byBooking(@PathVariable Integer bookingId) {
		return paymentService.byBooking(bookingId).stream().map(PaymentResponse::fromEntity).toList();
	}

	// NEW: GET /api/payment/agency/{agencyId}
	@GetMapping("/agency/{agencyId}")
	public List<PaymentResponse> byAgency(@PathVariable Integer agencyId) {
		return paymentService.byAgency(agencyId).stream().map(PaymentResponse::fromEntity).toList();
	}

	// NEW: GET /api/payment/office/{officeId}
	@GetMapping("/office/{officeId}")
	public List<PaymentResponse> byOffice(@PathVariable Integer officeId) {
		return paymentService.byOffice(officeId).stream().map(PaymentResponse::fromEntity).toList();
	}

}
