package com.demo.ticket.dto;
 
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
 
@Getter
@Setter
public class BusCreateDTO {
 
    @NotNull
    private Integer officeId;
 
    @NotBlank
    private String registrationNumber;
 
    @NotNull
    @Min(1)
    private Integer capacity;
 
    private String type; // AC, Sleeper, etc.
}
 
 