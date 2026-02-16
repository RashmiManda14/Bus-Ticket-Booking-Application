package com.demo.ticket.dto;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class DriverCreateDTO {
 
    @NotNull
    private Integer officeId;
    @NotNull
    private Integer addressId;
 
    @NotBlank
    private String licenseNumber;
    @NotBlank
    private String name;
    private String phone;
}
 
