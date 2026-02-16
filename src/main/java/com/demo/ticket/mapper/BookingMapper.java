package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.BookingCreateDTO;
import com.demo.ticket.dto.BookingResponse;
import com.demo.ticket.entity.Booking;
 
public final class BookingMapper {
 
    private BookingMapper() {}
 
    public static Booking toEntity(BookingCreateDTO dto) {
        if (dto == null) return null;
        Booking b = new Booking();
        b.setSeatNumber(dto.getSeatNumber());
        b.setStatus(dto.getStatus()); // String ("Booked" | "Available")
        // trip set in service by dto.tripId
        return b;
    }
 
    public static BookingResponse toResponse(Booking b) {
        if (b == null) return null;
        return BookingResponse.builder()
                .id(b.getId())
                .tripId(b.getTrip() != null ? b.getTrip().getId() : null)
                .seatNumber(b.getSeatNumber())
                .status(b.getStatus() != null ? String.valueOf(b.getStatus()) : null)
                .build();
    }
}
 