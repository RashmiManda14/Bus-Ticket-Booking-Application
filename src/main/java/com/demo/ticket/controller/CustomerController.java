package com.demo.ticket.controller;

package com.demo.ticket.controller;

import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.demo.ticket.dto.CustomerCreateDTO;
import com.demo.ticket.dto.CustomerResponse;
import com.demo.ticket.entity.Customer;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.CustomerRepository;
import com.demo.ticket.serviceImpl.CustomerServiceImpl;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

	private final CustomerServiceImpl customerService;
	private final CustomerRepository customerRepo;

	// ---------------------------------------------------------
	// GET ALL
	// ---------------------------------------------------------

	@GetMapping
	public List<CustomerResponse> getAll() {
		return customerService.getAll().stream().map(CustomerResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// GET BY ID
	// ---------------------------------------------------------

	@GetMapping("/{customerId}")
	public CustomerResponse getById(@PathVariable Integer customerId) {
		Customer c = customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
		return CustomerResponse.fromEntity(c);
	}

	// ---------------------------------------------------------
	// GET BY EMAIL
	// ---------------------------------------------------------

	@GetMapping("/email/{email}")
	public List<CustomerResponse> getByEmail(@PathVariable String email) {
		return customerRepo.findByEmail(email).stream().map(CustomerResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// GET BY PHONE (path param)
	// ---------------------------------------------------------

	// ---------------------------------------------------------
	// GET BY PHONE (query param — CSV has /api/customers/phone)
	// ---------------------------------------------------------

	@GetMapping("/phone")
	public List<CustomerResponse> getByPhoneQuery(@RequestParam("phone") String phone) {
		return customerRepo.findByPhone(phone).stream().map(CustomerResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// GET BY CITY (schema allows: address.city)
	// ---------------------------------------------------------

	@GetMapping("/city/{city}")
	public List<CustomerResponse> byCity(@PathVariable String city) {
		return customerRepo.findAll().stream()
				.filter(c -> c.getAddress() != null && city.equalsIgnoreCase(c.getAddress().getCity()))
				.map(CustomerResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// GET BY STATE (schema: address.state)
	// ---------------------------------------------------------

	@GetMapping("/state/{state}")
	public List<CustomerResponse> byState(@PathVariable String state) {
		return customerRepo.findAll().stream()
				.filter(c -> c.getAddress() != null && state.equalsIgnoreCase(c.getAddress().getState()))
				.map(CustomerResponse::fromEntity).toList();
	}

	// ---------------------------------------------------------
	// GET BY COUNTRY — NOT POSSIBLE in schema → return 400
	// ---------------------------------------------------------

	@GetMapping("/country/{country}")
	public ResponseEntity<Map<String, Object>> byCountry(@PathVariable String country) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", 400);
		body.put("message", "Country filter not supported. No 'country' column in database schema.");
		return ResponseEntity.badRequest().body(body);
	}

//
//    // ---------------------------------------------------------
//    // DTO for Generic Update
//    // ---------------------------------------------------------

//    @Data
//    public static class CustomerUpdateDTO {
//        private Integer id;
//        private String name;
//        private String email;
//        private String phone;
//    }
	@PostMapping
	public ResponseEntity<Map<String, String>> create(@Valid @RequestBody CustomerCreateDTO dto) {

		customerService.create(dto.getAddressId(),
				Customer.builder().name(dto.getName()).email(dto.getEmail()).phone(dto.getPhone()).build());

		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Record inserted successfully"));
	}

	@PutMapping("/update/{id}/ln")
	public ResponseEntity<Map<String, String>> updateLastName(@PathVariable Integer id, @RequestParam String ln) {

		customerService.updateLastName(id, ln);

		return ResponseEntity.ok(Map.of("message", "Last name updated successfully"));
	}

	@PutMapping("/update/{id}/email")
	public ResponseEntity<Map<String, String>> updateEmail(@PathVariable Integer id, @RequestParam String email) {

		customerService.updateEmail(id, email);

		return ResponseEntity.ok(Map.of("message", "Email updated successfully"));
	}

	@PutMapping
	public ResponseEntity<Map<String, String>> updateCustomer(@RequestParam("id") Integer customerId,
			@Valid @RequestBody CustomerCreateDTO dto) {

		String name = dto.getName();
		String email = dto.getEmail() == null ? null : dto.getEmail().trim();
		String phone = dto.getPhone() == null ? null : dto.getPhone().trim();

		// Optional: strip non-digits (if you want to allow formatted input like
		// +91-123...)
		// phone = (phone == null ? null : phone.replaceAll("\\D", ""));

		if (phone != null && !phone.isBlank() && !phone.matches("\\d{10}")) {
			// Keep the response shape consistent: JSON with message
			return ResponseEntity.badRequest()
					.body(Map.of("message", "ContactNumber must be exactly 10 digits (0-9)."));
		}

		customerService.updateCustomer(customerId, name, email, phone, dto.getAddressId());

		return ResponseEntity.ok(Map.of("message", "Record updated successfully"));
	}

}
