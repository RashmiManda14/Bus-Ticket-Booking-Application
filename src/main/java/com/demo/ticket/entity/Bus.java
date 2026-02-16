package com.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "buses", indexes = @Index(name = "idx_buses_office", columnList = "office_id"), uniqueConstraints = @UniqueConstraint(name = "uk_bus_registration", columnNames = "registration_number"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "bus_id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "office_id", nullable = false, foreignKey = @ForeignKey(name = "fk_bus_office"))
	private AgencyOffice office;

	@Column(name = "registration_number", length = 20, nullable = false)
	private String registrationNumber;

	@Column(name = "capacity")
	private Integer capacity;

	@Column(name = "type", length = 50)
	private String type;
}
