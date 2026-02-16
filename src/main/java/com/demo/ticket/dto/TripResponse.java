package com.demo.ticket.dto;
 
 
import lombok.*;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import com.demo.ticket.entity.Trip;
 
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class TripResponse {
    private Integer id;
    private Integer routeId;
    private Integer busId;
    private Integer boardingAddressId;
    private Integer droppingAddressId;
 
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
 
    private Integer driver1Id;
    private Integer driver2Id;
 
    private Integer availableSeats;
    private BigDecimal fare;
    private LocalDateTime tripDate;
 
    public static TripResponse fromEntity(Trip t) {
        return TripResponse.builder()
                .id(t.getId())
                .routeId(t.getRoute() != null ? t.getRoute().getId() : null)
                .busId(t.getBus() != null ? t.getBus().getId() : null)
                .boardingAddressId(t.getBoardingAddress() != null ? t.getBoardingAddress().getId() : null)
                .droppingAddressId(t.getDroppingAddress() != null ? t.getDroppingAddress().getId() : null)
                .departureTime(t.getDepartureTime())
                .arrivalTime(t.getArrivalTime())
                .driver1Id(t.getDriver1() != null ? t.getDriver1().getId() : null)
                .driver2Id(t.getDriver2() != null ? t.getDriver2().getId() : null)
                .availableSeats(t.getAvailableSeats())
                .fare(t.getFare())
                .tripDate(t.getTripDate())
                .build();
    }
}
 