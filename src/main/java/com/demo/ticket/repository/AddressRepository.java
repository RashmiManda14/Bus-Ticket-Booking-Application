package com.demo.ticket.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Address;
 
public interface AddressRepository extends JpaRepository<Address, Integer> {
}
 
 