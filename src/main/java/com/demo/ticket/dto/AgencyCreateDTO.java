package com.demo.ticket.dto;
 
 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
 
@Getter @Setter
public class AgencyCreateDTO {
    @NotBlank
    private String name;
 
    private String contactPersonName;
 
    @Email
    private String email;
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone number must be exactly 10 digits")
       
 
    private String phone;
}
 