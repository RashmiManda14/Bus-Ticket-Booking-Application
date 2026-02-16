package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.demo.ticket.entity.AgencyOffice;
import com.demo.ticket.entity.Bus;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AgencyOfficeRepository;
import com.demo.ticket.repository.BusRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusServiceImpl {

	private final BusRepository busRepo;
	private final AgencyOfficeRepository officeRepo;

	public Bus get(Integer id) {
		return busRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bus not found"));
	}

	public List<Bus> getAll() {
		return busRepo.findAll();
	}

	public List<Bus> getByOffice(Integer officeId) {
		return busRepo.findByOffice_Id(officeId);
	}

	public Bus create(Integer officeId, Bus bus) {

		boolean exists = busRepo.existsByRegistrationNumberIgnoreCase(bus.getRegistrationNumber());
		if (exists) {
			throw new ResourceNotFoundException("Registration number already exists");
		}

		AgencyOffice office = officeRepo.findById(officeId)
				.orElseThrow(() -> new ResourceNotFoundException("Office not found"));
		bus.setOffice(office);

		return busRepo.save(bus);
	}

	public Bus update(Integer busId, Integer officeId, Bus updatedData) {

		Bus existing = busRepo.findById(busId).orElseThrow(() -> new ResourceNotFoundException("Bus not found"));

		AgencyOffice office = officeRepo.findById(officeId)
				.orElseThrow(() -> new ResourceNotFoundException("Office not found"));

		existing.setOffice(office);
		existing.setRegistrationNumber(updatedData.getRegistrationNumber());
		existing.setCapacity(updatedData.getCapacity());
		existing.setType(updatedData.getType());

		return busRepo.save(existing);
	}
//  public void delete(Integer id) {
//      busRepo.delete(get(id));
//  }
}
