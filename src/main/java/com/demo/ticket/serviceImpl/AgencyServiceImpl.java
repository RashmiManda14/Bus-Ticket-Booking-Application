package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.demo.ticket.entity.Agency;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AgencyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyServiceImpl {

	private final AgencyRepository agencyRepository;

	public Agency create(Agency agency) {
		// Normalize inputs to avoid whitespace-based duplicates
		String name = agency.getName() != null ? agency.getName().trim() : null;
		String email = agency.getEmail() != null ? agency.getEmail().trim() : null;
		agency.setName(name);
		agency.setEmail(email);

		// --- Duplicate checks ---
		if (name != null && agencyRepository.existsByNameIgnoreCase(name)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Agency already exists with the same name");
		}

		if (name != null && email != null && agencyRepository.existsByNameIgnoreCaseAndEmailIgnoreCase(name, email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Agency already exists with the same name & email");
		}

		return agencyRepository.save(agency);
	}

	public Agency get(Integer id) {
		return agencyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
	}

	public List<Agency> getAll() {
		return agencyRepository.findAll();
	}

	public Agency update(Integer id, Agency updated) {
		Agency agency = get(id);
		agency.setName(updated.getName());
		agency.setContactPersonName(updated.getContactPersonName());
		agency.setEmail(updated.getEmail());
		agency.setPhone(updated.getPhone());
		return agencyRepository.save(agency);
	}
}
