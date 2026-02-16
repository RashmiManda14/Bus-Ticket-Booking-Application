package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.ReviewCreateDTO;
import com.demo.ticket.dto.ReviewResponse;
import com.demo.ticket.entity.Review;
 
public final class ReviewMapper {
 
    private ReviewMapper() {}
 
    public static Review toEntity(ReviewCreateDTO dto) {
        if (dto == null) return null;
        Review r = new Review();
        r.setRating(dto.getRating());
        r.setComment(dto.getComment());
        // trip & customer set in service by ids
        return r;
    }
 
    public static ReviewResponse toResponse(Review r) {
        if (r == null) return null;
        return ReviewResponse.builder()
                .id(r.getId())
                .tripId(r.getTrip() != null ? r.getTrip().getId() : null)
                .customerId(r.getCustomer() != null ? r.getCustomer().getId() : null)
                .rating(r.getRating())
                .comment(r.getComment())
                .reviewDate(r.getReviewDate())
                .build();
    }
}
 
 