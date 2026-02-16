package com.demo.ticket.mapper;
 
 
 
import com.demo.ticket.dto.AgencyOfficeCreateDTO;
import com.demo.ticket.dto.AgencyOfficeResponse;
import com.demo.ticket.entity.AgencyOffice;
 
public final class AgencyOfficeMapper {
 
    private AgencyOfficeMapper() {}
 
    public static AgencyOffice toEntity(AgencyOfficeCreateDTO dto) {
        if (dto == null) return null;
        AgencyOffice o = new AgencyOffice();
        o.setOfficeMail(dto.getOfficeMail());
        o.setOfficeContactPersonName(dto.getOfficeContactPersonName());
        o.setOfficeContactNumber(dto.getOfficeContactNumber());
        // agency & officeAddress are set in service by IDs from dto
        return o;
    }
 
    public static AgencyOfficeResponse toResponse(AgencyOffice o) {
        if (o == null) return null;
        return AgencyOfficeResponse.builder()
                .id(o.getId())
                .agencyId(o.getAgency() != null ? o.getAgency().getId() : null)
                .officeMail(o.getOfficeMail())
                .officeContactPersonName(o.getOfficeContactPersonName())
                .officeContactNumber(o.getOfficeContactNumber())
                .officeAddressId(o.getOfficeAddress() != null ? o.getOfficeAddress().getId() : null)
                .build();
    }
}
 
 