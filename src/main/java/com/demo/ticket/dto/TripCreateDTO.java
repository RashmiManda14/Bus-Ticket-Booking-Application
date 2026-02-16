package com.demo.ticket.dto;
 
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Getter
@Setter
public class TripCreateDTO {
 
    @NotNull
    private Integer routeId;
    @NotNull
    private Integer busId;
 
    @NotNull
    private Integer boardingAddressId;
    @NotNull
    private Integer droppingAddressId;
 
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
 
    @NotNull
    private Integer driver1Id;
    @NotNull
    private Integer driver2Id;
 
    private Integer availableSeats;
    private BigDecimal fare;
    private LocalDateTime tripDate;
}
 
 
 