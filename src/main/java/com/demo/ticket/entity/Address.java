package com.demo.ticket.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id")
	private Integer id;

	@NotBlank
	@Column(name = "street", length = 255, nullable = false)
	private String street;

	@NotBlank
	@Column(name = "city", length = 125, nullable = false)
	private String city;

	@NotBlank
	@Column(name = "state", length = 125, nullable = false)
	private String state;

	@NotBlank
	@Column(name = "zip_code", length = 10, nullable = false)
	private String zipCode;
}
