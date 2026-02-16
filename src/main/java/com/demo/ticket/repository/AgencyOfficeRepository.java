package com.demo.ticket.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.AgencyOffice;
 
import java.util.List;
 
public interface AgencyOfficeRepository extends JpaRepository<AgencyOffice, Integer> {
 
    List<AgencyOffice> findByAgency_Id(Integer agencyId);
 
    boolean existsByOfficeMailIgnoreCaseAndIdNot(String email, Integer id);
    boolean existsByOfficeContactNumberAndIdNot(String newPhone, Integer id);
 
}
 
 