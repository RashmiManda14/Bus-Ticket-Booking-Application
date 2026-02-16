package com.demo.ticket.repository;
 
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Driver;
 
import java.util.List;
 
public interface DriverRepository extends JpaRepository<Driver, Integer> {
 
    List<Driver> findByOffice_Id(Integer officeId);
 
    List<Driver> findByAddress_Id(Integer addressId);
 
    Driver findByLicenseNumber(String licenseNumber);
}
 
 
 