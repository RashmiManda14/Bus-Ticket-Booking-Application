package com.demo.ticket.dto;
 
 
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
 
@Getter @Setter
public class PaymentCreateDTO {
 
    @NotNull private Integer bookingId;
    @NotNull private Integer customerId;
 
    @NotNull private BigDecimal amount;
 
    private LocalDateTime paymentDate;   // optional; server can set if null
    private String paymentStatus;        // "Success" | "Failed"
}
 
 
 