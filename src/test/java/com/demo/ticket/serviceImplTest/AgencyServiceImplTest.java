package com.demo.ticket.serviceImplTest;
 
import com.demo.ticket.entity.Agency;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AgencyRepository;
import com.demo.ticket.serviceImpl.AgencyServiceImpl;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
/**
 * Unit tests for AgencyServiceImpl using JUnit5 and Mockito.
 */
@ExtendWith(MockitoExtension.class)
class AgencyServiceImplTest {
 
    @Mock
    private AgencyRepository agencyRepository;
 
    private AgencyServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new AgencyServiceImpl(agencyRepository);
    }
 
    // -------------------- create() tests --------------------
 
    @Test
    void create_shouldTrimAndSave_whenUnique() {
        // Arrange
        Agency input = Agency.builder()
                .name("  Acme Travels  ")       // leading/trailing spaces
                .contactPersonName(" Alice ")
                .email("  alice@acme.com  ")
                .phone("9999999999")
                .build();
 
        when(agencyRepository.existsByNameIgnoreCase("Acme Travels")).thenReturn(false);
        when(agencyRepository.existsByNameIgnoreCaseAndEmailIgnoreCase("Acme Travels", "alice@acme.com"))
                .thenReturn(false);
 
        Agency saved = Agency.builder()
                .id(1)
                .name("Acme Travels")
                .contactPersonName(" Alice ")
                .email("alice@acme.com")
                .phone("9999999999")
                .build();
 
        // capture the agency passed to save to assert trimming
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> {
            Agency arg = invocation.getArgument(0);
            // simulate DB assigning id
            arg.setId(1);
            return arg;
        });
 
        // Act
        Agency result = service.create(input);
 
        // Assert
        assertNotNull(result.getId(), "Saved agency should have an id");
        assertEquals("Acme Travels", result.getName(), "Name must be trimmed");
        assertEquals("alice@acme.com", result.getEmail(), "Email must be trimmed");
        assertEquals(" Alice ", result.getContactPersonName()); // unchanged
        assertEquals("9999999999", result.getPhone());
 
        verify(agencyRepository).existsByNameIgnoreCase("Acme Travels");
        verify(agencyRepository).existsByNameIgnoreCaseAndEmailIgnoreCase("Acme Travels", "alice@acme.com");
        verify(agencyRepository).save(any(Agency.class));
        verifyNoMoreInteractions(agencyRepository);
    }
 
    @Test
    void create_shouldThrowConflict_whenNameExists() {
        // Arrange
        Agency input = Agency.builder()
                .name("Acme")
                .email("contact@acme.com")
                .build();
 
        when(agencyRepository.existsByNameIgnoreCase("Acme")).thenReturn(true);
 
        // Act
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create(input));
 
        // Assert
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode(),
                "Status must be 409 CONFLICT when name already exists");
        assertTrue(ex.getReason().contains("same name"),
                "Reason should mention duplicate name");
 
        verify(agencyRepository).existsByNameIgnoreCase("Acme");
        verifyNoMoreInteractions(agencyRepository);
    }
 
    @Test
    void create_shouldThrowConflict_whenNameAndEmailExist() {
        // Arrange
        Agency input = Agency.builder()
                .name("Acme")
                .email("contact@acme.com")
                .build();
 
        when(agencyRepository.existsByNameIgnoreCase("Acme")).thenReturn(false);
        when(agencyRepository.existsByNameIgnoreCaseAndEmailIgnoreCase("Acme", "contact@acme.com"))
                .thenReturn(true);
 
        // Act
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create(input));
 
        // Assert
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode(),
                "Status must be 409 when name+email already exist");
        assertTrue(ex.getReason().contains("same name & email"),
                "Reason should mention duplicate name & email");
 
        verify(agencyRepository).existsByNameIgnoreCase("Acme");
        verify(agencyRepository).existsByNameIgnoreCaseAndEmailIgnoreCase("Acme", "contact@acme.com");
        verifyNoMoreInteractions(agencyRepository);
    }
 
    // -------------------- get() tests --------------------
 
    @Test
    void get_shouldReturnEntity_whenFound() {
        // Arrange
        Agency agency = Agency.builder()
                .id(10)
                .name("Zeta")
                .build();
 
        when(agencyRepository.findById(10)).thenReturn(Optional.of(agency));
 
        // Act
        Agency result = service.get(10);
 
        // Assert
        assertSame(agency, result, "Should return the same instance from repository");
 
        verify(agencyRepository).findById(10);
        verifyNoMoreInteractions(agencyRepository);
    }
 
    @Test
    void get_shouldThrowNotFound_whenMissing() {
        // Arrange
        when(agencyRepository.findById(99)).thenReturn(Optional.empty());
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.get(99));
 
        verify(agencyRepository).findById(99);
        verifyNoMoreInteractions(agencyRepository);
    }
 
    // -------------------- getAll() tests --------------------
 
    @Test
    void getAll_shouldReturnList() {
        // Arrange
        List<Agency> list = Arrays.asList(
                Agency.builder().id(1).name("A").build(),
                Agency.builder().id(2).name("B").build()
        );
        when(agencyRepository.findAll()).thenReturn(list);
 
        // Act
        List<Agency> result = service.getAll();
 
        // Assert
        assertEquals(2, result.size(), "Should return all agencies");
        assertEquals("A", result.get(0).getName());
        assertEquals("B", result.get(1).getName());
 
        verify(agencyRepository).findAll();
        verifyNoMoreInteractions(agencyRepository);
    }
 
    // -------------------- update() tests --------------------
 
    @Test
    void update_shouldCopyFieldsAndSave() {
        // Arrange
        Agency existing = Agency.builder()
                .id(5)
                .name("Old")
                .contactPersonName("Old CP")
                .email("old@x.com")
                .phone("1111111111")
                .build();
 
        Agency updated = Agency.builder()
                .name("New")
                .contactPersonName("New CP")
                .email("new@x.com")
                .phone("2222222222")
                .build();
 
        when(agencyRepository.findById(5)).thenReturn(Optional.of(existing));
        when(agencyRepository.save(any(Agency.class))).thenAnswer(invocation -> invocation.getArgument(0));
 
        // Act
        Agency result = service.update(5, updated);
 
        // Assert
        assertEquals("New", result.getName());
        assertEquals("New CP", result.getContactPersonName());
        assertEquals("new@x.com", result.getEmail());
        assertEquals("2222222222", result.getPhone());
        assertEquals(5, result.getId(), "ID must remain the same");
 
        verify(agencyRepository).findById(5);
        verify(agencyRepository).save(existing); // same instance updated & saved
        verifyNoMoreInteractions(agencyRepository);
    }
}
 
 
