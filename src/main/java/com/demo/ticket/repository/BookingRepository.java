package com.demo.ticket.repository;
 
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Booking;
 
import java.util.List;
 
public interface BookingRepository extends JpaRepository<Booking, Integer> {
 
    List<Booking> findByTrip_Id(Integer tripId);
 
    Booking findByTrip_IdAndSeatNumber(Integer tripId, Integer seatNumber);
}
 
 
 