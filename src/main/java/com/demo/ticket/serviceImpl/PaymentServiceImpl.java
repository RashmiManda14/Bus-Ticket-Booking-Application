package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.demo.ticket.entity.Booking;
import com.demo.ticket.entity.Customer;
import com.demo.ticket.entity.Payment;
import com.demo.ticket.entity.Trip;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.BookingRepository;
import com.demo.ticket.repository.CustomerRepository;
import com.demo.ticket.repository.PaymentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {

	private final PaymentRepository paymentRepo;
	private final BookingRepository bookingRepo;
	private final CustomerRepository customerRepo;

	// ---------------------------------------------------------
	// CREATE Payment (Duplicate Prevention Included)
	// ---------------------------------------------------------
	public Payment create(Integer bookingId, Integer customerId, Payment payment) {

		Booking booking = bookingRepo.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		Customer customer = customerRepo.findById(customerId)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		// ---------------------------------------------------------
		// DUPLICATE CHECK: A booking cannot have multiple payments
		// ---------------------------------------------------------
		boolean exists = paymentRepo.existsByBooking_Id(bookingId);

		if (exists) {
			// 409 Conflict fits business rule violations
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment already exists for this booking");
		}

		payment.setBooking(booking);
		payment.setCustomer(customer);

		return paymentRepo.save(payment);
	}

	// ---------------------------------------------------------
	// FILTERS
	// ---------------------------------------------------------

	public List<Payment> byCustomer(Integer customerId) {
		return paymentRepo.findByCustomer_Id(customerId);
	}

	public List<Payment> byBooking(Integer bookingId) {
		return paymentRepo.findByBooking_Id(bookingId);
	}

	public List<Payment> byAgency(Integer agencyId) {
		return paymentRepo.findAll().stream().filter(p -> {
			Booking b = p.getBooking();
			if (b == null)
				return false;
			Trip t = b.getTrip();
			if (t == null || t.getBus() == null || t.getBus().getOffice() == null
					|| t.getBus().getOffice().getAgency() == null)
				return false;
			return agencyId.equals(t.getBus().getOffice().getAgency().getId());
		}).toList();
	}

	public List<Payment> byOffice(Integer officeId) {
		return paymentRepo.findAll().stream().filter(p -> {
			Booking b = p.getBooking();
			if (b == null)
				return false;
			Trip t = b.getTrip();
			if (t == null || t.getBus() == null || t.getBus().getOffice() == null)
				return false;
			return officeId.equals(t.getBus().getOffice().getId());
		}).toList();
	}
}
