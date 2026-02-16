package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.PaymentCreateDTO;
import com.demo.ticket.dto.PaymentResponse;
import com.demo.ticket.entity.Payment;
import com.demo.ticket.entity.PaymentStatus;
 
public final class PaymentMapper {
 
    private PaymentMapper() {}
 
    public static Payment toEntity(PaymentCreateDTO dto) {
        if (dto == null) return null;
        Payment p = new Payment();
        p.setAmount(dto.getAmount());
        p.setPaymentDate(dto.getPaymentDate());
        p.setPaymentStatus(toPaymentStatus(dto.getPaymentStatus())); // enum
        // booking & customer set in service by ids
        return p;
    }
 
    public static PaymentResponse toResponse(Payment p) {
        if (p == null) return null;
        return PaymentResponse.builder()
                .id(p.getId())
                .bookingId(p.getBooking() != null ? p.getBooking().getId() : null)
                .customerId(p.getCustomer() != null ? p.getCustomer().getId() : null)
                .amount(p.getAmount())
                .paymentDate(p.getPaymentDate())
                .paymentStatus(fromPaymentStatus(p.getPaymentStatus())) // "SUCCESS"/"FAILED"
                .build();
    }
 
    // Helpers
    private static PaymentStatus toPaymentStatus(String status) {
        if (status == null) return null;
        String s = status.trim().toLowerCase();
        if (s.startsWith("s")) return PaymentStatus.SUCCESS;
        if (s.startsWith("f")) return PaymentStatus.FAILED;
        return null; // or throw IllegalArgumentException
    }
 
    private static String fromPaymentStatus(PaymentStatus status) {
        return status == null ? null : status.name();
        // If you want "Success"/"Failed" exactly:
        // return status == null ? null : (status == PaymentStatus.SUCCESS ? "Success" : "Failed");
    }
}
 