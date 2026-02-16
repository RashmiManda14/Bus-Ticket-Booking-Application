package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.demo.ticket.entity.Trip;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AddressRepository;
import com.demo.ticket.repository.BusRepository;
import com.demo.ticket.repository.DriverRepository;
import com.demo.ticket.repository.RouteRepository;
import com.demo.ticket.repository.TripRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripServiceImpl {

	private final TripRepository tripRepo;
	private final RouteRepository routeRepo;
	private final BusRepository busRepo;
	private final DriverRepository driverRepo;
	private final AddressRepository addressRepo;

	public Trip create(Trip trip, Integer routeId, Integer busId, Integer driver1Id, Integer driver2Id,
			Integer boardingId, Integer droppingId) {

		trip.setRoute(routeRepo.findById(routeId).orElseThrow(() -> new ResourceNotFoundException("Route not found")));

		trip.setBus(busRepo.findById(busId).orElseThrow(() -> new ResourceNotFoundException("Bus not found")));

		trip.setDriver1(
				driverRepo.findById(driver1Id).orElseThrow(() -> new ResourceNotFoundException("Driver1 not found")));

		trip.setDriver2(
				driverRepo.findById(driver2Id).orElseThrow(() -> new ResourceNotFoundException("Driver2 not found")));

		trip.setBoardingAddress(addressRepo.findById(boardingId)
				.orElseThrow(() -> new ResourceNotFoundException("Boarding Address not found")));

		trip.setDroppingAddress(addressRepo.findById(droppingId)
				.orElseThrow(() -> new ResourceNotFoundException("Dropping Address not found")));

		return tripRepo.save(trip);
	}

	public Trip get(Integer id) {
		return tripRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
	}

	public List<Trip> fromCity(String fromCity) {
		return tripRepo.findByRoute_FromCity(fromCity);
	}

	public List<Trip> toCity(String toCity) {
		return tripRepo.findByRoute_ToCity(toCity);
	}

	public List<Trip> byBusType(String type) {
		return tripRepo.findByBus_Type(type);
	}

	public List<Trip> byTripLocalDate(LocalDate date) {
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = start.plusDays(1);
		return tripRepo.findByTripDateBetween(start, end);
	}

	public List<Trip> search(String fromCity, String toCity, LocalDate date, String busType) {
		String fc = fromCity == null ? "" : fromCity.trim();
		String tc = toCity == null ? "" : toCity.trim();
		LocalDateTime start = date.atStartOfDay();
		LocalDateTime end = start.plusDays(1);

		if (busType == null || busType.isBlank()) {
			return tripRepo.findAllByFromToAndDate(fc, tc, start, end);
		} else {
			return tripRepo.findAllByFromToDateAndBusType(fc, tc, start, end, busType.trim());
		}
	}

	public Trip update(Integer id, Integer routeId, Integer busId, Integer driver1Id, Integer driver2Id,
			Integer boardingId, Integer droppingId, LocalDateTime departureTime, LocalDateTime arrivalTime,
			Integer availableSeats, BigDecimal fare, LocalDateTime tripDate) {

		Trip trip = tripRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

// Set updated fields
		trip.setRoute(routeRepo.findById(routeId).orElseThrow(() -> new ResourceNotFoundException("Route not found")));

		trip.setBus(busRepo.findById(busId).orElseThrow(() -> new ResourceNotFoundException("Bus not found")));

		trip.setDriver1(
				driverRepo.findById(driver1Id).orElseThrow(() -> new ResourceNotFoundException("Driver1 not found")));

		trip.setDriver2(
				driverRepo.findById(driver2Id).orElseThrow(() -> new ResourceNotFoundException("Driver2 not found")));

		trip.setBoardingAddress(addressRepo.findById(boardingId)
				.orElseThrow(() -> new ResourceNotFoundException("Boarding address not found")));

		trip.setDroppingAddress(addressRepo.findById(droppingId)
				.orElseThrow(() -> new ResourceNotFoundException("Dropping address not found")));

		trip.setDepartureTime(departureTime);
		trip.setArrivalTime(arrivalTime);
		trip.setAvailableSeats(availableSeats);
		trip.setFare(fare);
		trip.setTripDate(tripDate);

		return tripRepo.save(trip);
	}
}
