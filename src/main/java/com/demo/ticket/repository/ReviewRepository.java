package com.demo.ticket.repository;
 
 
import org.springframework.data.jpa.repository.JpaRepository;
 
import com.demo.ticket.entity.Review;
 
import java.util.List;
 
public interface ReviewRepository extends JpaRepository<Review, Integer> {
 
    List<Review> findByTrip_Id(Integer tripId);
 
    List<Review> findByCustomer_Id(Integer customerId);
 
    boolean existsByTrip_IdAndCustomer_Id(Integer tripId, Integer customerId);
   
 
}
 
 
 