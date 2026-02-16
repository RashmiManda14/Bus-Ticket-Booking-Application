package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.BusCreateDTO;
import com.demo.ticket.dto.BusResponse;
import com.demo.ticket.entity.Bus;
 
public final class BusMapper {
 
    private BusMapper() {}
 
    public static Bus toEntity(BusCreateDTO dto) {
        if (dto == null) return null;
        Bus b = new Bus();
        b.setRegistrationNumber(dto.getRegistrationNumber());
        b.setCapacity(dto.getCapacity());
        b.setType(dto.getType());
        // office set in service by dto.officeId
        return b;
    }
 
    public static BusResponse toResponse(Bus b) {
        if (b == null) return null;
        return BusResponse.builder()
                .id(b.getId())
                .officeId(b.getOffice() != null ? b.getOffice().getId() : null)
                .registrationNumber(b.getRegistrationNumber())
                .capacity(b.getCapacity())
                .type(b.getType())
                .build();
    }
}
 
 