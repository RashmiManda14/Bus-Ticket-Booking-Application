package com.demo.ticket.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "reviews", indexes = { @Index(name = "idx_reviews_route", columnList = "route_id"),
//		@Index(name = "idx_reviews_trip", columnList = "trip_id"),
//		@Index(name = "idx_reviews_customer", columnList = "customer_id") })
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Review {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "review_id")
//	private Integer id;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "route_id", foreignKey = @ForeignKey(name = "fk_review_route"))
//	private Route route;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "trip_id", foreignKey = @ForeignKey(name = "fk_review_trip"))
//	private Trip trip;
//
//	@ManyToOne(fetch = FetchType.LAZY, optional = false)
//	@JoinColumn(name = "customer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_review_customer"))
//	private Customer customer;
//
//	@Column(name = "rating", nullable = false)
//	private Integer rating;
//
//	@Column(name = "comment", columnDefinition = "TEXT")
//	private String comment;
//
//	@Column(name = "review_date")
//	private LocalDateTime reviewDate;
//}
//package com.demo.ticket.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Review {

 @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer id;

 @ManyToOne(optional = false, fetch = FetchType.LAZY)
 @JoinColumn(name = "trip_id", nullable = false)
 private Trip trip;

 @ManyToOne(optional = false, fetch = FetchType.LAZY)
 @JoinColumn(name = "customer_id", nullable = false)
 private Customer customer;

 @ManyToOne(optional = false, fetch = FetchType.LAZY)
 @JoinColumn(name = "route_id", nullable = false)
 private Route route;

 @Column(nullable = false)
 private Integer rating; // 1..5

 @Column(length = 1000)
 private String comment;

 @Column(nullable = false)
 private OffsetDateTime reviewDate;
}
