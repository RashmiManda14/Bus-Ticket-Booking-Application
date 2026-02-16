package com.demo.ticket.repository;
 
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Bus;
 
import java.util.List;
 
public interface BusRepository extends JpaRepository<Bus, Integer> {
 
    List<Bus> findByOffice_Id(Integer officeId);
 
    Bus findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumberIgnoreCase(String registrationNumber);
}
 
 
 