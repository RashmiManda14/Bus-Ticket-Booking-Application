package com.demo.ticket.serviceImplTest;
 
 
import com.demo.ticket.entity.Address;
import com.demo.ticket.entity.Agency;
import com.demo.ticket.entity.AgencyOffice;
import com.demo.ticket.exception.ConflictException;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AgencyOfficeRepository;
import com.demo.ticket.repository.AgencyRepository;
import com.demo.ticket.serviceImpl.AgencyOfficeServiceImpl;
import com.demo.ticket.repository.AddressRepository;
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
class AgencyOfficeServiceImplTest {
 
    @Mock private AgencyOfficeRepository officeRepo;
    @Mock private AgencyRepository agencyRepo;
    @Mock private AddressRepository addressRepo;
 
    private AgencyOfficeServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new AgencyOfficeServiceImpl(officeRepo, agencyRepo, addressRepo);
    }
 
    // ---------------------- create(...) ----------------------
 
    @Test
    void create_shouldSetAgencyAndSave_whenAgencyExists() {
        // Arrange
        Integer agencyId = 101;
        Agency agency = Agency.builder().id(agencyId).name("Acme").build();
        when(agencyRepo.findById(agencyId)).thenReturn(Optional.of(agency));
 
        AgencyOffice office = AgencyOffice.builder()
                .officeMail("office@acme.com")
                .officeContactPersonName("Alice")
                .officeContactNumber("9999999999")
                .build();
 
        when(officeRepo.save(any(AgencyOffice.class))).thenAnswer(inv -> {
            AgencyOffice saved = inv.getArgument(0);
            saved.setId(1);
            return saved;
        });
 
        // Act
        AgencyOffice saved = service.create(agencyId, office);
 
        // Assert
        assertNotNull(saved.getId(), "Saved office should have ID set");
        assertSame(agency, saved.getAgency(), "Service must set the agency on office before saving");
 
        verify(agencyRepo).findById(agencyId);
        verify(officeRepo).save(office);
        verifyNoMoreInteractions(agencyRepo, officeRepo, addressRepo);
    }
 
    @Test
    void create_shouldThrowNotFound_whenAgencyMissing() {
        // Arrange
        Integer agencyId = 404;
        when(agencyRepo.findById(agencyId)).thenReturn(Optional.empty());
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(agencyId, new AgencyOffice()));
 
        verify(agencyRepo).findById(agencyId);
        verifyNoInteractions(officeRepo);
    }
 
    // ---------------------- get(...) ----------------------
 
    @Test
    void get_shouldReturnOffice_whenFound() {
        // Arrange
        AgencyOffice office = AgencyOffice.builder().id(7).build();
        when(officeRepo.findById(7)).thenReturn(Optional.of(office));
 
        // Act
        AgencyOffice result = service.get(7);
 
        // Assert
        assertSame(office, result, "Service should return the same instance from repository");
        verify(officeRepo).findById(7);
        verifyNoMoreInteractions(officeRepo, agencyRepo, addressRepo);
    }
 
    @Test
    void get_shouldThrowNotFound_whenMissing() {
        // Arrange
        when(officeRepo.findById(99)).thenReturn(Optional.empty());
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.get(99));
        verify(officeRepo).findById(99);
        verifyNoMoreInteractions(officeRepo);
        verifyNoInteractions(agencyRepo, addressRepo);
    }
 
    // ---------------------- getByAgency(...) ----------------------
 
    @Test
    void getByAgency_shouldReturnListFromRepo() {
        // Arrange
        List<AgencyOffice> list = Arrays.asList(
                AgencyOffice.builder().id(1).build(),
                AgencyOffice.builder().id(2).build()
        );
        when(officeRepo.findByAgency_Id(42)).thenReturn(list);
 
        // Act
        List<AgencyOffice> result = service.getByAgency(42);
 
        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
 
        verify(officeRepo).findByAgency_Id(42);
        verifyNoMoreInteractions(officeRepo, agencyRepo, addressRepo);
    }
 
    // ---------------------- update(...) ----------------------
 
   
 
   
    @Test
    void update_shouldThrowConflict_whenDuplicateEmail() {
        // Arrange
        Integer officeId = 9;
        AgencyOffice existing = AgencyOffice.builder()
                .id(officeId)
                .officeMail("old@mail.com")
                .build();
        when(officeRepo.findById(officeId)).thenReturn(Optional.of(existing));
 
        AgencyOffice payload = AgencyOffice.builder()
                .officeMail("dup@mail.com")
                .build();
 
        when(officeRepo.existsByOfficeMailIgnoreCaseAndIdNot("dup@mail.com", officeId)).thenReturn(true);
 
        // Act + Assert
        assertThrows(ConflictException.class, () -> service.update(officeId, payload, null));
 
        verify(officeRepo).findById(officeId);
        verify(officeRepo).existsByOfficeMailIgnoreCaseAndIdNot("dup@mail.com", officeId);
        verify(officeRepo, never()).save(any());
        verifyNoInteractions(addressRepo);
    }
 
   
 
   
}
 
 
