package com.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bookings", indexes = @Index(name = "idx_bookings_trip", columnList = "trip_id"), uniqueConstraints = @UniqueConstraint(name = "uk_booking_trip_seat", columnNames = {
		"trip_id", "seat_number" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "booking_id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "trip_id", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_trip"))
	private Trip trip;

	@Column(name = "seat_number", nullable = false)
	private Integer seatNumber;

	// SMALLINT in DB; keep as Integer to match schema without altering it
	@Column(name = "status", nullable = false)
	private String status;
}
