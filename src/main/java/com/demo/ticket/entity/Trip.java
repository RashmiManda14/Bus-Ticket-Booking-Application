package com.demo.ticket.entity;
 
 
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "trips",
       indexes = {
           @Index(name = "idx_trips_route", columnList = "route_id"),
           @Index(name = "idx_trips_bus", columnList = "bus_id"),
           @Index(name = "idx_trips_boarding_address", columnList = "boarding_address_id"),
           @Index(name = "idx_trips_dropping_address", columnList = "dropping_address_id"),
           @Index(name = "idx_trips_driver1", columnList = "driver1_driver_id"),
           @Index(name = "idx_trips_driver2", columnList = "driver2_driver_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "route_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trip_route"))
    private Route route;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bus_id", nullable = false, foreignKey = @ForeignKey(name = "fk_trip_bus"))
    private Bus bus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boarding_address_id", foreignKey = @ForeignKey(name = "fk_trip_boarding_address"))
    private Address boardingAddress;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dropping_address_id", foreignKey = @ForeignKey(name = "fk_trip_dropping_address"))
    private Address droppingAddress;
    @Column(name = "departure_time")
    private LocalDateTime departureTime;
    @Column(name = "arrival_time")
    private LocalDateTime arrivalTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver1_driver_id", foreignKey = @ForeignKey(name = "fk_trip_driver1"))
    private Driver driver1;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver2_driver_id", foreignKey = @ForeignKey(name = "fk_trip_driver2"))
    private Driver driver2;
    @Column(name = "available_seats")
    private Integer availableSeats;
    @Column(name = "fare", precision = 10, scale = 2)
    private BigDecimal fare;
    @Column(name = "trip_date")
    private LocalDateTime tripDate;
}
