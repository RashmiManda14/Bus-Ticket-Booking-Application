package com.demo.ticket.dto;
 
import com.demo.ticket.entity.Bus;
 
import lombok.*;
 
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusResponse {
    private Integer id;
    private Integer officeId;
    private String registrationNumber;
    private Integer capacity;
    private String type;
 
    public static BusResponse fromEntity(Bus b) {
        return BusResponse.builder().id(b.getId()).officeId(b.getOffice() != null ? b.getOffice().getId() : null)
                .registrationNumber(b.getRegistrationNumber()).capacity(b.getCapacity()).type(b.getType()).build();
    }
}
 
 
