package com.demo.ticket.dto;
 
 
import com.demo.ticket.entity.Driver;
 
import lombok.*;
 
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class DriverResponse {
    private Integer id;
    private Integer officeId;
    private Integer addressId;
    private String licenseNumber;
    private String name;
    private String phone;
 
    public static DriverResponse fromEntity(Driver d) {
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
 
 
 
