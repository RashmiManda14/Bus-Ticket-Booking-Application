package com.demo.ticket.mapper;


import com.demo.ticket.dto.AddressCreateDTO;
import com.demo.ticket.dto.AddressResponse;
import com.demo.ticket.entity.Address;
 
public final class AddressMapper {
 
    private AddressMapper() {}
 
    public static Address toEntity(AddressCreateDTO dto) {
        if (dto == null) return null;
        Address a = new Address();
        a.setStreet(dto.getStreet());      // If entity uses 'address' -> a.setAddress(dto.getStreet());
        a.setCity(dto.getCity());
        a.setState(dto.getState());
        a.setZipCode(dto.getZipCode());
        return a;
    }
 
    public static AddressResponse toResponse(Address a) {
        if (a == null) return null;
        return AddressResponse.builder()
                .id(a.getId())
                .street(a.getStreet())     // If entity uses 'address' -> .street(a.getAddress())
                .city(a.getCity())
                .state(a.getState())
                .zipCode(a.getZipCode())
                .build();
    }
}
 
 