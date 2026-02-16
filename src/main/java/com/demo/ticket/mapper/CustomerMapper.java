package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.CustomerCreateDTO;
import com.demo.ticket.dto.CustomerResponse;
import com.demo.ticket.entity.Customer;
 
public final class CustomerMapper {
 
    private CustomerMapper() {}
 
    public static Customer toEntity(CustomerCreateDTO dto) {
        if (dto == null) return null;
        Customer c = new Customer();
        c.setName(dto.getName());
        c.setEmail(dto.getEmail());
        c.setPhone(dto.getPhone());
        // address set in service by dto.addressId
        return c;
    }
 
    public static CustomerResponse toResponse(Customer c) {
        if (c == null) return null;
        return CustomerResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .addressId(c.getAddress() != null ? c.getAddress().getId() : null)
                .build();
    }
}
 
 
