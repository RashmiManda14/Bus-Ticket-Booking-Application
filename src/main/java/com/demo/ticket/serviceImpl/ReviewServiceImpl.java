package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.demo.ticket.entity.Customer;
import com.demo.ticket.entity.Review;
import com.demo.ticket.entity.Route;
import com.demo.ticket.entity.Trip;
import com.demo.ticket.exception.ConflictException;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.CustomerRepository;
import com.demo.ticket.repository.ReviewRepository;
import com.demo.ticket.repository.RouteRepository;
import com.demo.ticket.repository.TripRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl {

	private final ReviewRepository reviewRepo;
	private final TripRepository tripRepo;
	private final CustomerRepository customerRepo;
	private final RouteRepository routeRepo;

	@Transactional
	public Review create(Integer tripId, Integer customerId, Integer routeId, Review payload,
			java.time.OffsetDateTime reviewDate) {

		var trip = tripRepo.findById(tripId).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
		var customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
		var route = routeRepo.findById(routeId).orElseThrow(() -> new ResourceNotFoundException("Route not found"));

		Integer rating = payload.getRating();
		if (rating == null || rating < 1 || rating > 5) {
			throw new IllegalArgumentException("rating must be between 1 and 5");
		}

		// Example business rule: prevent duplicate reviews for same trip+customer
		if (reviewRepo.existsByTrip_IdAndCustomer_Id(tripId, customerId)) {
			throw new ConflictException("A review already exists for this trip and customer");
		}

		Review toSave = Review.builder().trip(trip).customer(customer).route(route).rating(rating)
				.comment(payload.getComment()).reviewDate(reviewDate).build();

		return reviewRepo.save(toSave);
	}

	// GET BY TRIP
	public List<Review> byTrip(Integer tripId) {
		return reviewRepo.findByTrip_Id(tripId);
	}

	// GET BY CUSTOMER
	public List<Review> byCustomer(Integer customerId) {
		return reviewRepo.findByCustomer_Id(customerId);
	}

	// GET BY OFFICE (trip → bus → office)
	public List<Review> byOffice(Integer officeId) {
		return reviewRepo.findAll().stream()
				.filter(r -> r.getTrip() != null && r.getTrip().getBus() != null
						&& r.getTrip().getBus().getOffice() != null
						&& r.getTrip().getBus().getOffice().getId().equals(officeId))
				.toList();
	}

	// GET BY AGENCY (trip → bus → office → agency)
	public List<Review> byAgency(Integer agencyId) {
		return reviewRepo.findAll().stream().filter(r -> r.getTrip() != null && r.getTrip().getBus() != null
				&& r.getTrip().getBus().getOffice().getAgency().getId().equals(agencyId)).toList();
	}

	// GET BY DRIVER (driver1 or driver2)
	public List<Review> byDriver(Integer driverId) {
		return reviewRepo.findAll().stream().filter(r -> r.getTrip() != null
				&& ((r.getTrip().getDriver1() != null && r.getTrip().getDriver1().getId().equals(driverId))
						|| (r.getTrip().getDriver2() != null && r.getTrip().getDriver2().getId().equals(driverId))))
				.toList();
	}

	public Review update(Integer reviewId, Integer tripId, Integer customerId, Integer rating, String comment) {

		Review review = reviewRepo.findById(reviewId)
				.orElseThrow(() -> new ResourceNotFoundException("Review not found"));

		if (tripId != null) {
			Trip trip = tripRepo.findById(tripId).orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
			review.setTrip(trip);
		}

		if (customerId != null) {
			Customer customer = customerRepo.findById(customerId)
					.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
			review.setCustomer(customer);
		}

		if (rating != null) {
			if (rating < 1 || rating > 5) {
				throw new IllegalArgumentException("Rating must be between 1 and 5");
			}
			review.setRating(rating);
		}

		if (comment != null) {
			review.setComment(comment);
		}

		return reviewRepo.save(review);
	}
}
