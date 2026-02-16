package com.demo.ticket.controller;

import com.demo.ticket.dto.AddressCreateDTO;
import com.demo.ticket.dto.AddressResponse;
import com.demo.ticket.entity.Address;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AddressRepository;
import com.demo.ticket.serviceImpl.AddressServiceImpl;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.stream.Collectors;
 
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
 
    private final AddressServiceImpl addressService;
    private final AddressRepository addressRepo;
 
}