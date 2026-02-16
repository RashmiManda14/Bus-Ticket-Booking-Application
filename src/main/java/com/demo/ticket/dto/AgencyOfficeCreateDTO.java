package com.demo.ticket.dto;
 
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
 
@Getter @Setter
public class AgencyOfficeCreateDTO {
 
    @NotNull
    private Integer agencyId;
 
    @Email
    private String officeMail;
 
    private String officeContactPersonName;
    private String officeContactNumber;
 
    // optional link to an existing Address
    private Integer officeAddressId;
}
 
 
