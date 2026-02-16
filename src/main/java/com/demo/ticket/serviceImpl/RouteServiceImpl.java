package com.demo.ticket.serviceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.demo.ticket.entity.Route;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.RouteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl {

	private final RouteRepository routeRepo;

	public Route create(Route route) {
		return routeRepo.save(route);
	}

	public Route get(Integer id) {
		return routeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Route not found"));
	}

	public List<Route> getAll() {
		return routeRepo.findAll();
	}

	public List<Route> getFrom(String fromCity) {
		return routeRepo.findByFromCity(fromCity);
	}

	public List<Route> getTo(String toCity) {
		return routeRepo.findByToCity(toCity);
	}
}
