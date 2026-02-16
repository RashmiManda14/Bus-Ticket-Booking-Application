package com.demo.ticket.serviceImplTest;
 
 
import com.demo.ticket.entity.*;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.*;
import com.demo.ticket.serviceImpl.TripServiceImpl;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class TripServiceImplTest {
 
    @Mock private TripRepository tripRepo;
    @Mock private RouteRepository routeRepo;
    @Mock private BusRepository busRepo;
    @Mock private DriverRepository driverRepo;
    @Mock private AddressRepository addressRepo;
 
    private TripServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new TripServiceImpl(tripRepo, routeRepo, busRepo, driverRepo, addressRepo);
    }
 
    // ========================== create(...) ==========================
 
    @Test
    void create_shouldSetAssociationsAndSave_whenAllDependenciesExist() {
        // Arrange input ids
        Integer routeId = 1, busId = 2, d1 = 3, d2 = 4, boardId = 5, dropId = 6;
 
        Route route = Route.builder().id(routeId).fromCity("A").toCity("B").build();
        Bus bus = Bus.builder().id(busId).build();
        Driver driver1 = Driver.builder().id(d1).build();
        Driver driver2 = Driver.builder().id(d2).build();
        Address boarding = Address.builder().id(boardId).build();
        Address dropping = Address.builder().id(dropId).build();
 
        when(routeRepo.findById(routeId)).thenReturn(Optional.of(route));
        when(busRepo.findById(busId)).thenReturn(Optional.of(bus));
        when(driverRepo.findById(d1)).thenReturn(Optional.of(driver1));
        when(driverRepo.findById(d2)).thenReturn(Optional.of(driver2));
        when(addressRepo.findById(boardId)).thenReturn(Optional.of(boarding));
        when(addressRepo.findById(dropId)).thenReturn(Optional.of(dropping));
 
        Trip toCreate = Trip.builder()
                .departureTime(LocalDateTime.of(2026, 1, 1, 10, 0))
                .arrivalTime(LocalDateTime.of(2026, 1, 1, 14, 0))
                .availableSeats(40)
                .fare(new BigDecimal("499.99"))
                .tripDate(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();
 
        when(tripRepo.save(any(Trip.class))).thenAnswer(inv -> {
            Trip t = inv.getArgument(0);
            t.setId(100);
            return t;
        });
 
        // Act
        Trip saved = service.create(toCreate, routeId, busId, d1, d2, boardId, dropId);
 
        // Assert
        assertNotNull(saved.getId());
        assertSame(route, saved.getRoute());
        assertSame(bus, saved.getBus());
        assertSame(driver1, saved.getDriver1());
        assertSame(driver2, saved.getDriver2());
        assertSame(boarding, saved.getBoardingAddress());
        assertSame(dropping, saved.getDroppingAddress());
        assertEquals(new BigDecimal("499.99"), saved.getFare());
        assertEquals(40, saved.getAvailableSeats());
 
        verify(routeRepo).findById(routeId);
        verify(busRepo).findById(busId);
        verify(driverRepo).findById(d1);
        verify(driverRepo).findById(d2);
        verify(addressRepo).findById(boardId);
        verify(addressRepo).findById(dropId);
        verify(tripRepo).save(toCreate);
        verifyNoMoreInteractions(tripRepo, routeRepo, busRepo, driverRepo, addressRepo);
    }
 
    @Test
    void create_shouldThrow_whenRouteNotFound() {
        when(routeRepo.findById(1)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(Trip.builder().build(), 1, 2, 3, 4, 5, 6));
 
        verify(routeRepo).findById(1);
        verifyNoMoreInteractions(routeRepo);
        verifyNoInteractions(busRepo, driverRepo, addressRepo, tripRepo);
    }
 
    @Test
    void create_shouldThrow_whenBusNotFound() {
        when(routeRepo.findById(1)).thenReturn(Optional.of(Route.builder().id(1).build()));
        when(busRepo.findById(2)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(Trip.builder().build(), 1, 2, 3, 4, 5, 6));
 
        verify(busRepo).findById(2);
        verifyNoInteractions(tripRepo);
    }
 
    @Test
    void create_shouldThrow_whenDriver1NotFound() {
        when(routeRepo.findById(1)).thenReturn(Optional.of(Route.builder().id(1).build()));
        when(busRepo.findById(2)).thenReturn(Optional.of(Bus.builder().id(2).build()));
        when(driverRepo.findById(3)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(Trip.builder().build(), 1, 2, 3, 4, 5, 6));
 
        verify(driverRepo).findById(3);
        verifyNoInteractions(tripRepo);
    }
 
    @Test
    void create_shouldThrow_whenDriver2NotFound() {
        when(routeRepo.findById(1)).thenReturn(Optional.of(Route.builder().id(1).build()));
        when(busRepo.findById(2)).thenReturn(Optional.of(Bus.builder().id(2).build()));
        when(driverRepo.findById(3)).thenReturn(Optional.of(Driver.builder().id(3).build()));
        when(driverRepo.findById(4)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(Trip.builder().build(), 1, 2, 3, 4, 5, 6));
 
        verify(driverRepo).findById(4);
        verifyNoInteractions(tripRepo);
    }
 
    @Test
    void create_shouldThrow_whenBoardingAddressNotFound() {
        when(routeRepo.findById(1)).thenReturn(Optional.of(Route.builder().id(1).build()));
        when(busRepo.findById(2)).thenReturn(Optional.of(Bus.builder().id(2).build()));
        when(driverRepo.findById(3)).thenReturn(Optional.of(Driver.builder().id(3).build()));
        when(driverRepo.findById(4)).thenReturn(Optional.of(Driver.builder().id(4).build()));
        when(addressRepo.findById(5)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(Trip.builder().build(), 1, 2, 3, 4, 5, 6));
 
        verify(addressRepo).findById(5);
        verifyNoInteractions(tripRepo);
    }
 
    @Test
    void create_shouldThrow_whenDroppingAddressNotFound() {
        when(routeRepo.findById(1)).thenReturn(Optional.of(Route.builder().id(1).build()));
        when(busRepo.findById(2)).thenReturn(Optional.of(Bus.builder().id(2).build()));
        when(driverRepo.findById(3)).thenReturn(Optional.of(Driver.builder().id(3).build()));
        when(driverRepo.findById(4)).thenReturn(Optional.of(Driver.builder().id(4).build()));
        when(addressRepo.findById(5)).thenReturn(Optional.of(Address.builder().id(5).build()));
        when(addressRepo.findById(6)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(Trip.builder().build(), 1, 2, 3, 4, 5, 6));
 
        verify(addressRepo).findById(6);
        verifyNoInteractions(tripRepo);
    }
 
    // ========================== get(...) ==========================
 
    @Test
    void get_shouldReturn_whenFound() {
        Trip t = Trip.builder().id(10).build();
        when(tripRepo.findById(10)).thenReturn(Optional.of(t));
 
        Trip res = service.get(10);
 
        assertSame(t, res);
        verify(tripRepo).findById(10);
        verifyNoMoreInteractions(tripRepo);
    }
 
    @Test
    void get_shouldThrow_whenMissing() {
        when(tripRepo.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(999));
        verify(tripRepo).findById(999);
    }
 
    // ========================== fromCity / toCity / byBusType ==========================
 
    @Test
    void fromCity_shouldDelegateToRepository() {
        List<Trip> list = Arrays.asList(Trip.builder().id(1).build(), Trip.builder().id(2).build());
        when(tripRepo.findByRoute_FromCity("Bangalore")).thenReturn(list);
 
        List<Trip> res = service.fromCity("Bangalore");
 
        assertEquals(2, res.size());
        verify(tripRepo).findByRoute_FromCity("Bangalore");
    }
 
    @Test
    void toCity_shouldDelegateToRepository() {
        List<Trip> list = Arrays.asList(Trip.builder().id(3).build());
        when(tripRepo.findByRoute_ToCity("Mysore")).thenReturn(list);
 
        List<Trip> res = service.toCity("Mysore");
 
        assertEquals(1, res.size());
        verify(tripRepo).findByRoute_ToCity("Mysore");
    }
 
    @Test
    void byBusType_shouldDelegateToRepository() {
        List<Trip> list = Arrays.asList(Trip.builder().id(4).build(), Trip.builder().id(5).build());
        when(tripRepo.findByBus_Type("AC")).thenReturn(list);
 
        List<Trip> res = service.byBusType("AC");
 
        assertEquals(2, res.size());
        verify(tripRepo).findByBus_Type("AC");
    }
 
    // ========================== byTripLocalDate(...) ==========================
 
    @Test
    void byTripLocalDate_shouldCallBetweenWithDayWindow() {
        LocalDate date = LocalDate.of(2026, 1, 2);
        ArgumentCaptor<LocalDateTime> startCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCap = ArgumentCaptor.forClass(LocalDateTime.class);
 
        when(tripRepo.findByTripDateBetween(any(), any())).thenReturn(Collections.emptyList());
 
        List<Trip> res = service.byTripLocalDate(date);
 
        assertNotNull(res);
        verify(tripRepo).findByTripDateBetween(startCap.capture(), endCap.capture());
 
        LocalDateTime expectedStart = date.atStartOfDay();
        LocalDateTime expectedEnd = expectedStart.plusDays(1);
 
        assertEquals(expectedStart, startCap.getValue());
        assertEquals(expectedEnd, endCap.getValue());
    }
 
    // ========================== search(...) ==========================
 
    @Test
    void search_withoutBusType_shouldCallFindAllByFromToAndDate() {
        LocalDate date = LocalDate.of(2026, 1, 3);
        ArgumentCaptor<String> fromCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> startCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCap = ArgumentCaptor.forClass(LocalDateTime.class);
 
        when(tripRepo.findAllByFromToAndDate(anyString(), anyString(), any(), any()))
                .thenReturn(Collections.singletonList(Trip.builder().id(1).build()));
 
        List<Trip> res = service.search("Bangalore", "Mysore", date, null);
 
        assertEquals(1, res.size());
 
        verify(tripRepo).findAllByFromToAndDate(fromCap.capture(), toCap.capture(), startCap.capture(), endCap.capture());
        assertEquals("Bangalore", fromCap.getValue());
        assertEquals("Mysore", toCap.getValue());
        assertEquals(date.atStartOfDay(), startCap.getValue());
        assertEquals(date.atStartOfDay().plusDays(1), endCap.getValue());
    }
 
    @Test
    void search_withBusType_shouldCallFindAllByFromToDateAndBusType() {
        LocalDate date = LocalDate.of(2026, 1, 4);
        ArgumentCaptor<String> fromCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> toCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> startCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endCap = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<String> typeCap = ArgumentCaptor.forClass(String.class);
 
        when(tripRepo.findAllByFromToDateAndBusType(anyString(), anyString(), any(), any(), anyString()))
                .thenReturn(Arrays.asList(Trip.builder().id(10).build(), Trip.builder().id(11).build()));
 
        List<Trip> res = service.search("Bangalore", "Mysore", date, "AC");
 
        assertEquals(2, res.size());
 
        verify(tripRepo).findAllByFromToDateAndBusType(
                fromCap.capture(), toCap.capture(), startCap.capture(), endCap.capture(), typeCap.capture());
 
        assertEquals("Bangalore", fromCap.getValue());
        assertEquals("Mysore", toCap.getValue());
        assertEquals(date.atStartOfDay(), startCap.getValue());
        assertEquals(date.atStartOfDay().plusDays(1), endCap.getValue());
        assertEquals("AC", typeCap.getValue());
    }
 
    // ========================== update(...) ==========================
 
    @Test
    void update_shouldReplaceAssociations_setFields_andSave() {
        Integer id = 100;
        Integer routeId = 1, busId = 2, d1 = 3, d2 = 4, boardId = 5, dropId = 6;
 
        Trip existing = Trip.builder()
                .id(id)
                .availableSeats(10)
                .fare(new BigDecimal("100.00"))
                .build();
 
        when(tripRepo.findById(id)).thenReturn(Optional.of(existing));
 
        Route route = Route.builder().id(routeId).fromCity("A").toCity("B").build();
        Bus bus = Bus.builder().id(busId).build();
        Driver driver1 = Driver.builder().id(d1).build();
        Driver driver2 = Driver.builder().id(d2).build();
        Address boarding = Address.builder().id(boardId).build();
        Address dropping = Address.builder().id(dropId).build();
 
        when(routeRepo.findById(routeId)).thenReturn(Optional.of(route));
        when(busRepo.findById(busId)).thenReturn(Optional.of(bus));
        when(driverRepo.findById(d1)).thenReturn(Optional.of(driver1));
        when(driverRepo.findById(d2)).thenReturn(Optional.of(driver2));
        when(addressRepo.findById(boardId)).thenReturn(Optional.of(boarding));
        when(addressRepo.findById(dropId)).thenReturn(Optional.of(dropping));
 
        LocalDateTime dep = LocalDateTime.of(2026, 2, 1, 9, 0);
        LocalDateTime arr = LocalDateTime.of(2026, 2, 1, 13, 0);
        Integer seats = 50;
        BigDecimal fare = new BigDecimal("799.50");
        LocalDateTime tripDate = LocalDateTime.of(2026, 2, 1, 0, 0);
 
        when(tripRepo.save(any(Trip.class))).thenAnswer(inv -> inv.getArgument(0));
 
        Trip updated = service.update(id, routeId, busId, d1, d2, boardId, dropId, dep, arr, seats, fare, tripDate);
 
        assertEquals(id, updated.getId());
        assertSame(route, updated.getRoute());
        assertSame(bus, updated.getBus());
        assertSame(driver1, updated.getDriver1());
        assertSame(driver2, updated.getDriver2());
        assertSame(boarding, updated.getBoardingAddress());
        assertSame(dropping, updated.getDroppingAddress());
        assertEquals(dep, updated.getDepartureTime());
        assertEquals(arr, updated.getArrivalTime());
        assertEquals(seats, updated.getAvailableSeats());
        assertEquals(fare, updated.getFare());
        assertEquals(tripDate, updated.getTripDate());
 
        verify(tripRepo).findById(id);
        verify(routeRepo).findById(routeId);
        verify(busRepo).findById(busId);
        verify(driverRepo).findById(d1);
        verify(driverRepo).findById(d2);
        verify(addressRepo).findById(boardId);
        verify(addressRepo).findById(dropId);
        verify(tripRepo).save(existing); // same instance updated & saved
        verifyNoMoreInteractions(tripRepo, routeRepo, busRepo, driverRepo, addressRepo);
    }
 
    @Test
    void update_shouldThrow_whenTripNotFound() {
        when(tripRepo.findById(999)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(999, 1, 2, 3, 4, 5, 6,
                        LocalDateTime.now(), LocalDateTime.now().plusHours(4), 10,
                        new BigDecimal("100.00"), LocalDateTime.now()));
 
        verify(tripRepo).findById(999);
        verifyNoMoreInteractions(tripRepo);
    }
 
    @Test
    void update_shouldThrow_whenRouteNotFound() {
        when(tripRepo.findById(1)).thenReturn(Optional.of(Trip.builder().id(1).build()));
        when(routeRepo.findById(404)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1, 404, 2, 3, 4, 5, 6,
                        LocalDateTime.now(), LocalDateTime.now().plusHours(1), 10,
                        new BigDecimal("100.00"), LocalDateTime.now()));
 
        verify(routeRepo).findById(404);
        verify(tripRepo, never()).save(any());
    }
}
 
 
 
