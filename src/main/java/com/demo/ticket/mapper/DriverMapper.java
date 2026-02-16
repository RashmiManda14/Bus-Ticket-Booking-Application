package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.DriverCreateDTO;
import com.demo.ticket.dto.DriverResponse;
import com.demo.ticket.entity.Driver;
 
public final class DriverMapper {
 
    private DriverMapper() {}
 
    public static Driver toEntity(DriverCreateDTO dto) {
        if (dto == null) return null;
        Driver d = new Driver();
        d.setLicenseNumber(dto.getLicenseNumber());
        d.setName(dto.getName());
        d.setPhone(dto.getPhone());
        // office & address set in service by ids
        return d;
    }
 
    public static DriverResponse toResponse(Driver d) {
        if (d == null) return null;
        return DriverResponse.builder()
                .id(d.getId())
                .officeId(d.getOffice() != null ? d.getOffice().getId() : null)
                .addressId(d.getAddress() != null ? d.getAddress().getId() : null)
                .licenseNumber(d.getLicenseNumber())
                .name(d.getName())
                .phone(d.getPhone())
                .build();
    }
}
 
 