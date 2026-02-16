package com.demo.ticket.dto;
import com.demo.ticket.entity.Address;
 
import lombok.*;
 
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class AddressResponse {
    private Integer id;
    private String street;  
    private String city;
    private String state;
    private String zipCode;
 
    public static AddressResponse fromEntity(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .street(a.getStreet())
                .city(a.getCity())
                .state(a.getState())
                .zipCode(a.getZipCode())
                .build();
    }
}
 