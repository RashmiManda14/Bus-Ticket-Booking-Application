package com.demo.ticket.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.ticket.entity.Payment;
 
import java.util.List;
 
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
 
    // Existing finders you already use
    List<Payment> findByBooking_Id(Integer bookingId);
 
    List<Payment> findByCustomer_Id(Integer customerId);
   
    boolean existsByBooking_Id(Integer bookingId);
 
   
}
 