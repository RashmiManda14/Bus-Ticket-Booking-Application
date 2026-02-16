package com.demo.ticket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class BookingCreateDTO {
 
    @NotNull
    private Integer tripId;
 
    @NotNull
    @Min(1)
    private Integer seatNumber;
 
    // optional (service can default to "Booked")
    private String status; // "Booked" | "Available"
}
 
 
