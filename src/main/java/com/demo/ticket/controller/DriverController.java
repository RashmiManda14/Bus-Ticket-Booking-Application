package com.demo.ticket.controller;


import com.demo.ticket.dto.DriverCreateDTO;
import com.demo.ticket.dto.DriverResponse;
import com.demo.ticket.entity.Driver;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.DriverRepository;
import com.demo.ticket.serviceImpl.DriverServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {
 
    private final DriverServiceImpl driverService;
    private final DriverRepository driverRepo;
 
}