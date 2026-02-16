package com.demo.ticket.serviceImplTest;
 
 
import com.demo.ticket.entity.Route;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.RouteRepository;
import com.demo.ticket.serviceImpl.RouteServiceImpl;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {
 
    @Mock
    private RouteRepository routeRepo;
 
    private RouteServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new RouteServiceImpl(routeRepo);
    }
 
    // ---------------------- create(...) ----------------------
 
    @Test
    void create_shouldSaveAndReturnRoute() {
        // Arrange
        Route toCreate = Route.builder()
                .fromCity("Bangalore")
                .toCity("Mysore")
                .breakPoints(2)
                .duration(180)
                .build();
 
        when(routeRepo.save(any(Route.class))).thenAnswer(inv -> {
            Route r = inv.getArgument(0);
            r.setId(1);
            return r;
        });
 
        // Act
        Route saved = service.create(toCreate);
 
        // Assert
        assertNotNull(saved.getId(), "Saved route should have an id");
        assertEquals("Bangalore", saved.getFromCity());
        assertEquals("Mysore", saved.getToCity());
        assertEquals(2, saved.getBreakPoints());
        assertEquals(180, saved.getDuration());
 
        verify(routeRepo).save(toCreate);
        verifyNoMoreInteractions(routeRepo);
    }
 
    // ---------------------- get(...) ----------------------
 
    @Test
    void get_shouldReturnRoute_whenFound() {
        // Arrange
        Route r = Route.builder().id(10).fromCity("A").toCity("B").build();
        when(routeRepo.findById(10)).thenReturn(Optional.of(r));
 
        // Act
        Route result = service.get(10);
 
        // Assert
        assertSame(r, result, "Should return the same instance from repository");
        verify(routeRepo).findById(10);
        verifyNoMoreInteractions(routeRepo);
    }
 
    @Test
    void get_shouldThrowNotFound_whenMissing() {
        // Arrange
        when(routeRepo.findById(999)).thenReturn(Optional.empty());
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.get(999));
        verify(routeRepo).findById(999);
        verifyNoMoreInteractions(routeRepo);
    }
 
    // ---------------------- getAll(...) ----------------------
 
    @Test
    void getAll_shouldReturnList() {
        // Arrange
        List<Route> list = Arrays.asList(
                Route.builder().id(1).fromCity("A").toCity("B").build(),
                Route.builder().id(2).fromCity("C").toCity("D").build()
        );
        when(routeRepo.findAll()).thenReturn(list);
 
        // Act
        List<Route> result = service.getAll();
 
        // Assert
        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getFromCity());
        assertEquals("D", result.get(1).getToCity());
 
        verify(routeRepo).findAll();
        verifyNoMoreInteractions(routeRepo);
    }
 
    // ---------------------- getFrom(...) ----------------------
 
    @Test
    void getFrom_shouldDelegateToRepository() {
        // Arrange
        String from = "Bangalore";
        List<Route> list = Arrays.asList(
                Route.builder().id(3).fromCity(from).toCity("Mysore").build(),
                Route.builder().id(4).fromCity(from).toCity("Chennai").build()
        );
        when(routeRepo.findByFromCity(from)).thenReturn(list);
 
        // Act
        List<Route> result = service.getFrom(from);
 
        // Assert
        assertEquals(2, result.size());
        assertEquals(from, result.get(0).getFromCity());
        assertEquals(from, result.get(1).getFromCity());
 
        verify(routeRepo).findByFromCity(from);
        verifyNoMoreInteractions(routeRepo);
    }
 
    // ---------------------- getTo(...) ----------------------
 
    @Test
    void getTo_shouldDelegateToRepository() {
        // Arrange
        String to = "Hyderabad";
        List<Route> list = Arrays.asList(
                Route.builder().id(5).fromCity("Bangalore").toCity(to).build(),
                Route.builder().id(6).fromCity("Chennai").toCity(to).build()
        );
        when(routeRepo.findByToCity(to)).thenReturn(list);
 
        // Act
        List<Route> result = service.getTo(to);
 
        // Assert
        assertEquals(2, result.size());
        assertEquals(to, result.get(0).getToCity());
        assertEquals(to, result.get(1).getToCity());
 
        verify(routeRepo).findByToCity(to);
        verifyNoMoreInteractions(routeRepo);
    }
}
 
 
