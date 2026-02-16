package com.demo.ticket.dto;
 
import com.demo.ticket.entity.Agency;
 
import lombok.*;
 
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyResponse {
    private Integer id;
    private String name;
    private String contactPersonName;
    private String email;
    private String phone;
 
    public static AgencyResponse fromEntity(Agency a) {
        return AgencyResponse.builder().id(a.getId()).name(a.getName()).contactPersonName(a.getContactPersonName())
                .email(a.getEmail()).phone(a.getPhone()).build();
    }
}
 
 