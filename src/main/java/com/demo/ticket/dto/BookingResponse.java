package com.demo.ticket.dto;
 
 
import com.demo.ticket.entity.Booking;
 
import lombok.*;
 
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class BookingResponse {
    private Integer id;
    private Integer tripId;
    private Integer seatNumber;
    private String status; // "Booked" | "Available" (or numeric -> stringified)
 
    public static BookingResponse fromEntity(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .tripId(b.getTrip() != null ? b.getTrip().getId() : null)
                .seatNumber(b.getSeatNumber())
                .status(b.getStatus() != null ? String.valueOf(b.getStatus()) : null)
                .build();
    }
}
 
 
 