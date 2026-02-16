package com.demo.ticket.repository;
 
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
 
import com.demo.ticket.entity.Trip;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
 
public interface TripRepository extends JpaRepository<Trip, Integer> {
 
    List<Trip> findByRoute_Id(Integer routeId);
 
    List<Trip> findByBus_Id(Integer busId);
 
    List<Trip> findByTripDate(LocalDateTime tripDate);
 
    List<Trip> findByRoute_FromCity(String fromCity);
 
    List<Trip> findByRoute_ToCity(String toCity);
 
    List<Trip> findByBus_Type(String type);
 
    List<Trip> findByDriver1_Id(Integer driverId);
 
    List<Trip> findByDriver2_Id(Integer driverId);
 // date-only read
    List<Trip> findByTripDateBetween(LocalDateTime start, LocalDateTime end);
 
    // combined filters (case-insensitive, full-day window)
    @Query("""
           select t from Trip t
            where lower(t.route.fromCity) = lower(:fromCity)
              and lower(t.route.toCity)   = lower(:toCity)
              and t.tripDate >= :start and t.tripDate < :end
           """)
    List<Trip> findAllByFromToAndDate(@Param("fromCity") String fromCity,
                                      @Param("toCity") String toCity,
                                      @Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);
 
    @Query("""
           select t from Trip t
            where lower(t.route.fromCity) = lower(:fromCity)
              and lower(t.route.toCity)   = lower(:toCity)
              and t.tripDate >= :start and t.tripDate < :end
              and lower(t.bus.type) = lower(:busType)
           """)
    List<Trip> findAllByFromToDateAndBusType(@Param("fromCity") String fromCity,
                                             @Param("toCity") String toCity,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("busType") String busType);
 
 
}
   
 
 
 
 