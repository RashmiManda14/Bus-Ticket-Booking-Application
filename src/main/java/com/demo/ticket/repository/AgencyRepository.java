package com.demo.ticket.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Agency;
 
public interface AgencyRepository extends JpaRepository<Agency, Integer> {
 
      boolean existsByNameIgnoreCase(String name);
 
        boolean existsByNameIgnoreCaseAndEmailIgnoreCase(String name, String email);
 
 
 
}
 
 