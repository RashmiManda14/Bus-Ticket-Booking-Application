package com.demo.ticket.dto;
 
 
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
 
@Getter @Setter
public class RouteCreateDTO {
    @NotBlank private String fromCity;
    @NotBlank private String toCity;
 
    private Integer breakPoints;
    private Integer duration; // minutes or hours per your entity
}