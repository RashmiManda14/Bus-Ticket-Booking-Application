package com.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers", indexes = @Index(name = "idx_customers_address", columnList = "address_id"), uniqueConstraints = @UniqueConstraint(name = "uk_customer_email", columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "customer_id")
	private Integer id;

	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "email", length = 255)
	private String email;

	@Column(name = "phone", length = 15)
	private String phone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", foreignKey = @ForeignKey(name = "fk_customer_address"))
	private Address address;
}
