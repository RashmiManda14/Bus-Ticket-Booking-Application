package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.demo.ticket.entity.Address;
import com.demo.ticket.entity.Agency;
import com.demo.ticket.entity.AgencyOffice;
import com.demo.ticket.exception.ConflictException;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AgencyOfficeRepository;
import com.demo.ticket.repository.AgencyRepository;
import com.demo.ticket.repository.AddressRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyOfficeServiceImpl {

	private final AgencyOfficeRepository officeRepo;
	private final AgencyRepository agencyRepo;
	private final AddressRepository addressRepo;

	public AgencyOffice create(Integer agencyId, AgencyOffice office) {
		Agency agency = agencyRepo.findById(agencyId)
				.orElseThrow(() -> new ResourceNotFoundException("Agency not found"));
		office.setAgency(agency);
		return officeRepo.save(office);
	}

	public AgencyOffice get(Integer id) {
		return officeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Office not found"));
	}

	public List<AgencyOffice> getByAgency(Integer agencyId) {
		return officeRepo.findByAgency_Id(agencyId);
	}

	// UPDATE: accept officeAddressId and set address if provided
	public AgencyOffice update(Integer id, AgencyOffice payload, Integer officeAddressId) { // <--- CHANGED SIG
		AgencyOffice existing = get(id);

		String newEmail = payload.getOfficeMail() == null ? null : payload.getOfficeMail().trim();
		String newPhone = payload.getOfficeContactNumber() == null ? null : payload.getOfficeContactNumber().trim();

		if (newEmail != null && !newEmail.isBlank() && officeRepo.existsByOfficeMailIgnoreCaseAndIdNot(newEmail, id)) {
			throw new ConflictException("Email already exists");
		}
		if (newPhone != null && !newPhone.isBlank() && officeRepo.existsByOfficeContactNumberAndIdNot(newPhone, id)) {
			throw new ConflictException("Phone number already exists");
		}

		if (payload.getOfficeMail() != null) {
			existing.setOfficeMail(newEmail);
		}
		if (payload.getOfficeContactNumber() != null) {
			existing.setOfficeContactNumber(newPhone);
		}
		if (payload.getOfficeContactPersonName() != null) {
			existing.setOfficeContactPersonName(payload.getOfficeContactPersonName());
		}

		// This is the line in your screenshot—make sure all imports and the field are
		// present
		if (officeAddressId != null) {
			Address address = addressRepo.findById(officeAddressId)
					.orElseThrow(() -> new ResourceNotFoundException("Address not found"));
			// Alternative (no Optional chaining):
			// Address address = addressRepo.getReferenceById(officeAddressId);
			// (Wrap with try/catch for EntityNotFoundException if you want 404)
			existing.setOfficeAddress(address);
		}

		return officeRepo.save(existing);
	}

//    public void delete(Integer id) {
//        officeRepo.delete(get(id));
//    }
//    

}
