package com.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agency_offices", indexes = { @Index(name = "idx_office_agency", columnList = "agency_id"),
		@Index(name = "idx_office_address", columnList = "office_address_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgencyOffice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "office_id")
	private Integer id;

	// Owning side; keep cascade OFF here (do not cascade from child to parent)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "agency_id", nullable = false, foreignKey = @ForeignKey(name = "fk_office_agency"))
	private Agency agency;

	@Column(name = "office_mail", length = 100)
	private String officeMail;

	@Column(name = "office_contact_person_name", length = 50)
	private String officeContactPersonName;

	@Column(name = "office_contact_number", length = 10)
	private String officeContactNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "office_address_id", foreignKey = @ForeignKey(name = "fk_office_address"))
	private Address officeAddress;
}
