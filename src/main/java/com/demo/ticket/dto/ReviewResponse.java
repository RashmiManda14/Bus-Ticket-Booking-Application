package com.demo.ticket.dto;
import lombok.*;
import java.time.OffsetDateTime;
import com.demo.ticket.entity.Review;
 
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Integer id;
    private Integer routeId;
    private Integer tripId;
    private Integer customerId;
    private Integer rating;
    private String comment;
    private OffsetDateTime reviewDate;
 
    public static ReviewResponse fromEntity(Review r) {
        if (r == null) return null;
        return ReviewResponse.builder()
                .id(r.getId())
                .routeId(r.getRoute() != null ? r.getRoute().getId() : null)
                .tripId(r.getTrip() != null ? r.getTrip().getId() : null)
                .customerId(r.getCustomer() != null ? r.getCustomer().getId() : null)
                .rating(r.getRating())
                .comment(r.getComment())
                .reviewDate(r.getReviewDate()) // should be OffsetDateTime in entity
                .build();
    }
}
 
 