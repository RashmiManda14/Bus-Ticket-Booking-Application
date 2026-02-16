package com.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers", indexes = { @Index(name = "idx_drivers_office", columnList = "office_id"),
		@Index(name = "idx_drivers_address", columnList = "address_id") }, uniqueConstraints = @UniqueConstraint(name = "uk_driver_license", columnNames = "license_number"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "driver_id")
	private Integer id;

	@Column(name = "license_number", length = 20, nullable = false)
	private String licenseNumber;

	@Column(name = "name", length = 125, nullable = false)
	private String name;

	@Column(name = "phone", length = 13)
	private String phone;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "office_id", nullable = false, foreignKey = @ForeignKey(name = "fk_driver_office"))
	private AgencyOffice office;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", foreignKey = @ForeignKey(name = "fk_driver_address"))
	private Address address;
}
