package com.demo.ticket.dto;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.demo.ticket.entity.Payment;

@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
   private Integer id;
   private Integer bookingId;
   private Integer customerId;
   private BigDecimal amount;
   private LocalDateTime paymentDate;
   private String paymentStatus; // "Success" | "Failed"

   public static PaymentResponse fromEntity(Payment p) {
       return PaymentResponse.builder()
               .id(p.getId())
               .bookingId(p.getBooking() != null ? p.getBooking().getId() : null)
               .customerId(p.getCustomer() != null ? p.getCustomer().getId() : null)
               .amount(p.getAmount())
               .paymentDate(p.getPaymentDate())
               .paymentStatus(p.getPaymentStatus() != null ? p.getPaymentStatus().toString() : null)
               .build();
   }
}


