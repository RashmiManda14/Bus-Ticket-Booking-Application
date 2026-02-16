package com.demo.ticket.dto;
 
 
import com.demo.ticket.entity.AgencyOffice;
 
import lombok.*;
 
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class AgencyOfficeResponse {
    private Integer id;
    private Integer agencyId;
    private String officeMail;
    private String officeContactPersonName;
    private String officeContactNumber;
    private Integer officeAddressId;
 
    public static AgencyOfficeResponse fromEntity(AgencyOffice o) {
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
 