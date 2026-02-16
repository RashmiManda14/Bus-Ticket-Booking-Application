package com.demo.ticket.mapper;
 
 
import com.demo.ticket.dto.AgencyCreateDTO;
import com.demo.ticket.dto.AgencyResponse;
import com.demo.ticket.entity.Agency;
 
public final class AgencyMapper {
 
    private AgencyMapper() {}
 
    public static Agency toEntity(AgencyCreateDTO dto) {
        if (dto == null) return null;
        Agency a = new Agency();
        a.setName(dto.getName());
        a.setContactPersonName(dto.getContactPersonName());
        a.setEmail(dto.getEmail());
        a.setPhone(dto.getPhone());
        return a;
    }
 
    public static AgencyResponse toResponse(Agency a) {
        if (a == null) return null;
        return AgencyResponse.builder()
                .id(a.getId())
                .name(a.getName())
                .contactPersonName(a.getContactPersonName())
                .email(a.getEmail())
                .phone(a.getPhone())
                .build();
    }
}
 
 
 