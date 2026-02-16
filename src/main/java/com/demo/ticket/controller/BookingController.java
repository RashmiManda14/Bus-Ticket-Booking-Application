package com.demo.ticket.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.demo.ticket.dto.BookingCreateDTO;
import com.demo.ticket.dto.BookingResponse;
import com.demo.ticket.entity.Booking;
import com.demo.ticket.serviceImpl.BookingServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

	private final BookingServiceImpl bookingService;

}
