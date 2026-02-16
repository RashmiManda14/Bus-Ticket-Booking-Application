package com.demo.ticket.dto;
 
 
import com.demo.ticket.entity.Route;
 
import lombok.*;
 
@Getter @Builder @NoArgsConstructor @AllArgsConstructor
public class RouteResponse {
    private Integer id;
    private String fromCity;
    private String toCity;
    private Integer breakPoints;
    private Integer duration;
 
    public static RouteResponse fromEntity(Route r) {
        return RouteResponse.builder()
                .id(r.getId())
                .fromCity(r.getFromCity())
                .toCity(r.getToCity())
                .breakPoints(r.getBreakPoints())
                .duration(r.getDuration())
                .build();
    }
}
 
 
 
