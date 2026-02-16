package com.demo.ticket.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Route;
 
import java.util.List;
 
public interface RouteRepository extends JpaRepository<Route, Integer> {
 
    List<Route> findByFromCity(String fromCity);
 
    List<Route> findByToCity(String toCity);
 
    Route findByFromCityAndToCity(String fromCity, String toCity);
}
 
 
 