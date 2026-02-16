package com.demo.ticket.mapper;
 
import com.demo.ticket.dto.RouteCreateDTO;
import com.demo.ticket.dto.RouteResponse;
import com.demo.ticket.entity.Route;
 
public final class RouteMapper {
 
    private RouteMapper() {}
 
    public static Route toEntity(RouteCreateDTO dto) {
        if (dto == null) return null;
        Route r = new Route();
        r.setFromCity(dto.getFromCity());
        r.setToCity(dto.getToCity());
        r.setBreakPoints(dto.getBreakPoints());
        r.setDuration(dto.getDuration());
        return r;
    }
 
    public static RouteResponse toResponse(Route r) {
        if (r == null) return null;
        return RouteResponse.builder()
                .id(r.getId())
                .fromCity(r.getFromCity())
                .toCity(r.getToCity())
                .breakPoints(r.getBreakPoints())
                .duration(r.getDuration())
                .build();
    }
}
 