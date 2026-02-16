package com.demo.ticket.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.OffsetDateTime;
 
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class ReviewCreateDTO {
 
    @NotNull(message = "tripId is required")
    private Integer tripId;
 
    @NotNull(message = "customerId is required")
    private Integer customerId;
 
    @NotNull(message = "routeId is required")
    private Integer routeId;
 
    @NotNull(message = "rating is required")
    @Min(value = 1, message = "rating must be at least 1")
    @Max(value = 5, message = "rating must be at most 5")
    private Integer rating;
 
    @Size(max = 1000, message = "comment must be at most 1000 characters")
    private String comment;
 
    @NotNull(message = "reviewDate is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") // accepts Z or ±hh:mm offsets
    private OffsetDateTime reviewDate;
}
 
 
