package com.demo.ticket.controller;
import jakarta.validation.Valid;
 
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
 
import com.demo.ticket.dto.AgencyCreateDTO;
import com.demo.ticket.dto.AgencyOfficeCreateDTO;
import com.demo.ticket.dto.AgencyOfficeResponse;
import com.demo.ticket.dto.AgencyResponse;
 
import com.demo.ticket.entity.AgencyOffice;
import com.demo.ticket.entity.Address;
import com.demo.ticket.entity.Agency;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AddressRepository;
import com.demo.ticket.repository.AgencyOfficeRepository;
import com.demo.ticket.serviceImpl.AgencyOfficeServiceImpl;
 
import java.net.URI;
import java.util.List;
import java.util.Map;
 
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/agencies")
public class AgencyOfficeController {
 
    private final AgencyOfficeServiceImpl officeService;
    private final AgencyOfficeRepository officeRepo;
    private final AddressRepository addressRepo;
   
    @PostMapping("/addoffice")
    public ResponseEntity<Map<String, String>> create(@Valid @RequestBody AgencyOfficeCreateDTO dto) {
 
        AgencyOffice office = AgencyOffice.builder()
                .officeMail(dto.getOfficeMail())
                .officeContactPersonName(dto.getOfficeContactPersonName())
                .officeContactNumber(dto.getOfficeContactNumber())
                .build();
 
        if (dto.getOfficeAddressId() != null) {
            Address address = addressRepo.findById(dto.getOfficeAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
            office.setOfficeAddress(address);
        }
 
        officeService.create(dto.getAgencyId(), office);
 
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "Record inserted successfully"));
    }
   
 
    // ---------------------------------------------------------
    // GET: /api/agencies/offices (all offices)
    // ---------------------------------------------------------
    @GetMapping("/offices")
    public List<AgencyOfficeResponse> getAllOffices() {
        return officeRepo.findAll().stream().map(AgencyOfficeResponse::fromEntity).toList();
    }
 
    // ---------------------------------------------------------
    // GET: /api/agencies/offices/{officeId}
    // ---------------------------------------------------------
    @GetMapping("/offices/{officeId}")
    public AgencyOfficeResponse getOffice(@PathVariable Integer officeId) {
        AgencyOffice office = officeRepo.findById(officeId)
                .orElseThrow(() -> new ResourceNotFoundException("Office not found"));
        return AgencyOfficeResponse.fromEntity(office);
    }
 
    // ---------------------------------------------------------
    // GET: /api/agencies/offices/agency/{agencyId}
    // (CSV contains conflict, so we separate cleanly)
    // ---------------------------------------------------------
    @GetMapping("/offices/agency/{agencyId}")
    public List<AgencyOfficeResponse> getByAgency(@PathVariable Integer agencyId) {
        return officeService.getByAgency(agencyId).stream().map(AgencyOfficeResponse::fromEntity).toList();
    }
 
   
    @PutMapping("/offices/{officeId}")
    public ResponseEntity<Map<String, String>> updateOffice(
            @PathVariable Integer officeId,
            @Valid @RequestBody OfficeUpdateDTO dto) {
 
        AgencyOffice payload = AgencyOffice.builder()
                .officeMail(dto.getOfficeMail())
                .officeContactPersonName(dto.getOfficeContactPersonName())
                .officeContactNumber(dto.getOfficeContactNumber())
                .build();
 
        officeService.update(officeId, payload, dto.getOfficeAddressId());
 
        return ResponseEntity.ok(Map.of("message", "Record updated successfully"));
    }
//
    @Data
    public static class OfficeUpdateDTO {
        private String officeMail;
        private String officeContactPersonName;
        private String officeContactNumber;
        private Integer officeAddressId;
    }
 
}
 