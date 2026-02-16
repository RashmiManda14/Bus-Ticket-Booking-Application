package com.demo.ticket.dto;
 
import com.demo.ticket.entity.Customer;
 
import lombok.*;
 
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Integer id;
    private String name;
    private String email;
    private String phone;
    private Integer addressId;
 
    public static CustomerResponse fromEntity(Customer c) {
        return CustomerResponse.builder().id(c.getId()).name(c.getName()).email(c.getEmail()).phone(c.getPhone())
                .addressId(c.getAddress() != null ? c.getAddress().getId() : null).build();
    }
}
 
 