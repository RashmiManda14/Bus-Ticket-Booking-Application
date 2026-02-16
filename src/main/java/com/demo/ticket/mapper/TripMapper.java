package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.TripCreateDTO;
import com.demo.ticket.dto.TripResponse;
import com.demo.ticket.entity.Trip;
 
public final class TripMapper {
 
    private TripMapper() {}
 
    public static Trip toEntity(TripCreateDTO dto) {
        if (dto == null) return null;
        Trip t = new Trip();
        t.setDepartureTime(dto.getDepartureTime());
        t.setArrivalTime(dto.getArrivalTime());
        t.setAvailableSeats(dto.getAvailableSeats());
        t.setFare(dto.getFare());
        t.setTripDate(dto.getTripDate());
        // route, bus, drivers, boarding/dropping set in service via ids
        return t;
    }
 
    public static TripResponse toResponse(Trip t) {
        if (t == null) return null;
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
 
 