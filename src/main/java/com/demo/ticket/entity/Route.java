package com.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "route_id")
	private Integer id;

	@Column(name = "from_city", length = 255, nullable = false)
	private String fromCity;

	@Column(name = "to_city", length = 255, nullable = false)
	private String toCity;

	@Column(name = "break_points")
	private Integer breakPoints;

	@Column(name = "duration")
	private Integer duration;

	
}
