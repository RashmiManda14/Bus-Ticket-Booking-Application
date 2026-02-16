package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.demo.ticket.entity.Address;
import com.demo.ticket.entity.Customer;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AddressRepository;
import com.demo.ticket.repository.CustomerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl {

	private final CustomerRepository customerRepo;
	private final AddressRepository addressRepo;

	public Customer create(Integer addressId, Customer customer) {

		// Normalize inputs (optional but recommended)
		String email = customer.getEmail() != null ? customer.getEmail().trim().toLowerCase() : null;
		String phone = customer.getPhone() != null ? customer.getPhone().trim() : null;

		// Duplicate checks (no DB schema change needed)
		if (email != null && !email.isEmpty() && customerRepo.existsByEmailIgnoreCase(email)) {
			throw new ResourceNotFoundException("Email already exists");
		}

		if (phone != null && !phone.isEmpty() && customerRepo.existsByPhone(phone)) {
			throw new ResourceNotFoundException("Phone already exists");
		}

		Address address = addressRepo.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address not found"));

		customer.setAddress(address);
		customer.setEmail(email); // keep normalized
		customer.setPhone(phone); // keep normalized

		return customerRepo.save(customer);
	}

	public Customer updateLastName(Integer id, String lastName) {
		Customer customer = customerRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		// Assuming the "name" field stores the full name
		customer.setName(lastName);

		return customerRepo.save(customer);
	}

	public Customer updateEmail(Integer id, String email) {
		// (Optional) quick format check; you can also use javax/jakarta validation if
		// you prefer
		if (email == null || !email.contains("@")) {
			throw new IllegalArgumentException("Invalid email format");
		}

		Customer c = customerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		c.setEmail(email);
		return customerRepo.save(c);
	}

	public Customer updateCustomer(Integer customerId, String name, String email, String phone, Integer addressId) {

		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		if (name != null)
			customer.setName(name);
		if (email != null)
			customer.setEmail(email);
		if (phone != null)
			customer.setPhone(phone);

		if (addressId != null) {
			customer.setAddress(addressRepo.findById(addressId)
					.orElseThrow(() -> new ResourceNotFoundException("Address not found")));
		}

		return customerRepo.save(customer);
	}

	public Customer get(Integer id) {
		return customerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
	}

	public List<Customer> getAll() {
		return customerRepo.findAll();
	}

	public Customer getCustomer(Integer customerId) {
		// TODO Auto-generated method stub
		return null;
	}

}
