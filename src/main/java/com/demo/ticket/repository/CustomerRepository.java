package com.demo.ticket.repository;
 
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Customer;
 
import java.util.List;
 
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
 
    List<Customer> findByEmail(String email);
 
    List<Customer> findByPhone(String phone);
 
    List<Customer> findByAddress_Id(Integer addressId);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);
 
}
 
 
 