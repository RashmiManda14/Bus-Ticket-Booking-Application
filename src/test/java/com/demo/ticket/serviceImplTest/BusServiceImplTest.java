package com.demo.ticket.serviceImplTest;
 
 
import com.demo.ticket.entity.AgencyOffice;
import com.demo.ticket.entity.Bus;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AgencyOfficeRepository;
import com.demo.ticket.repository.BusRepository;
import com.demo.ticket.serviceImpl.BusServiceImpl;
 
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
class BusServiceImplTest {
 
    @Mock private BusRepository busRepo;
    @Mock private AgencyOfficeRepository officeRepo;
 
    private BusServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new BusServiceImpl(busRepo, officeRepo);
    }
 
    // ---------------------- get(...) ----------------------
 
    @Test
    void get_shouldReturn_whenFound() {
        Bus bus = Bus.builder().id(1).registrationNumber("KA01AB1111").build();
        when(busRepo.findById(1)).thenReturn(Optional.of(bus));
 
        Bus result = service.get(1);
 
        assertSame(bus, result, "Should return the same instance from repo");
        verify(busRepo).findById(1);
        verifyNoMoreInteractions(busRepo, officeRepo);
    }
 
    @Test
    void get_shouldThrow_whenMissing() {
        when(busRepo.findById(99)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class, () -> service.get(99));
 
        verify(busRepo).findById(99);
        verifyNoMoreInteractions(busRepo);
        verifyNoInteractions(officeRepo);
    }
 
    // ---------------------- getAll(...) ----------------------
 
    @Test
    void getAll_shouldReturnList() {
        List<Bus> list = Arrays.asList(
                Bus.builder().id(1).registrationNumber("KA01AB1111").build(),
                Bus.builder().id(2).registrationNumber("KA01AB2222").build()
        );
        when(busRepo.findAll()).thenReturn(list);
 
        List<Bus> result = service.getAll();
 
        assertEquals(2, result.size());
        assertEquals("KA01AB1111", result.get(0).getRegistrationNumber());
        assertEquals("KA01AB2222", result.get(1).getRegistrationNumber());
 
        verify(busRepo).findAll();
        verifyNoMoreInteractions(busRepo);
        verifyNoInteractions(officeRepo);
    }
 
    // ---------------------- getByOffice(...) ----------------------
 
    @Test
    void getByOffice_shouldReturnListFromRepo() {
        List<Bus> list = Arrays.asList(
                Bus.builder().id(10).build(),
                Bus.builder().id(11).build()
        );
        when(busRepo.findByOffice_Id(7)).thenReturn(list);
 
        List<Bus> result = service.getByOffice(7);
 
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals(11, result.get(1).getId());
 
        verify(busRepo).findByOffice_Id(7);
        verifyNoMoreInteractions(busRepo, officeRepo);
    }
 
    // ---------------------- create(...) ----------------------
 
    @Test
    void create_shouldSave_whenRegistrationUnique_andOfficeExists() {
        Integer officeId = 5;
        Bus input = Bus.builder()
                .registrationNumber("KA01AB1234")
                .capacity(40)
                .type("AC")
                .build();
 
        when(busRepo.existsByRegistrationNumberIgnoreCase("KA01AB1234")).thenReturn(false);
 
        AgencyOffice office = AgencyOffice.builder().id(officeId).build();
        when(officeRepo.findById(officeId)).thenReturn(Optional.of(office));
 
        // capture the bus passed to save to assert office set
        ArgumentCaptor<Bus> busCaptor = ArgumentCaptor.forClass(Bus.class);
        when(busRepo.save(any(Bus.class))).thenAnswer(inv -> {
            Bus b = inv.getArgument(0);
            b.setId(100);
            return b;
        });
 
        Bus saved = service.create(officeId, input);
 
        assertNotNull(saved.getId());
        assertEquals("KA01AB1234", saved.getRegistrationNumber());
        assertEquals(40, saved.getCapacity());
        assertEquals("AC", saved.getType());
        assertNotNull(saved.getOffice(), "Office must be set on bus");
        assertEquals(officeId, saved.getOffice().getId());
 
        verify(busRepo).existsByRegistrationNumberIgnoreCase("KA01AB1234");
        verify(officeRepo).findById(officeId);
        verify(busRepo).save(busCaptor.capture());
        assertSame(office, busCaptor.getValue().getOffice(), "Service must set office before saving");
        verifyNoMoreInteractions(busRepo, officeRepo);
    }
 
    @Test
    void create_shouldThrow_whenRegistrationAlreadyExists() {
        Bus input = Bus.builder().registrationNumber("KA01AB1234").build();
        when(busRepo.existsByRegistrationNumberIgnoreCase("KA01AB1234")).thenReturn(true);
 
        assertThrows(ResourceNotFoundException.class, () -> service.create(1, input));
 
        verify(busRepo).existsByRegistrationNumberIgnoreCase("KA01AB1234");
        verifyNoMoreInteractions(busRepo);
        verifyNoInteractions(officeRepo);
    }
 
    @Test
    void create_shouldThrow_whenOfficeNotFound() {
        Integer officeId = 404;
        Bus input = Bus.builder().registrationNumber("KA01AB1234").build();
 
        when(busRepo.existsByRegistrationNumberIgnoreCase("KA01AB1234")).thenReturn(false);
        when(officeRepo.findById(officeId)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class, () -> service.create(officeId, input));
 
        verify(busRepo).existsByRegistrationNumberIgnoreCase("KA01AB1234");
        verify(officeRepo).findById(officeId);
        verifyNoMoreInteractions(busRepo, officeRepo);
    }
 
    // ---------------------- update(...) ----------------------
 
    @Test
    void update_shouldSetOfficeAndFields_thenSave() {
        Integer busId = 10;
        Integer officeId = 8;
 
        Bus existing = Bus.builder()
                .id(busId)
                .registrationNumber("OLD-REG")
                .capacity(30)
                .type("NON-AC")
                .build();
 
        Bus updates = Bus.builder()
                .registrationNumber("NEW-REG")
                .capacity(45)
                .type("AC")
                .build();
 
        AgencyOffice office = AgencyOffice.builder().id(officeId).build();
 
        when(busRepo.findById(busId)).thenReturn(Optional.of(existing));
        when(officeRepo.findById(officeId)).thenReturn(Optional.of(office));
        when(busRepo.save(any(Bus.class))).thenAnswer(inv -> inv.getArgument(0));
 
        Bus result = service.update(busId, officeId, updates);
 
        assertEquals(busId, result.getId());
        assertEquals("NEW-REG", result.getRegistrationNumber());
        assertEquals(45, result.getCapacity());
        assertEquals("AC", result.getType());
        assertNotNull(result.getOffice());
        assertEquals(officeId, result.getOffice().getId());
 
        verify(busRepo).findById(busId);
        verify(officeRepo).findById(officeId);
        verify(busRepo).save(existing); // same instance updated & saved
        verifyNoMoreInteractions(busRepo, officeRepo);
    }
 
    @Test
    void update_shouldThrow_whenBusNotFound() {
        when(busRepo.findById(999)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(999, 1, Bus.builder().build()));
 
        verify(busRepo).findById(999);
        verifyNoMoreInteractions(busRepo);
        verifyNoInteractions(officeRepo);
    }
 
    @Test
    void update_shouldThrow_whenOfficeNotFound() {
        Integer busId = 1, missingOfficeId = 404;
        when(busRepo.findById(busId)).thenReturn(Optional.of(Bus.builder().id(busId).build()));
        when(officeRepo.findById(missingOfficeId)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(busId, missingOfficeId, Bus.builder().build()));
 
        verify(busRepo).findById(busId);
        verify(officeRepo).findById(missingOfficeId);
        verifyNoMoreInteractions(busRepo, officeRepo);
    }
}
 
