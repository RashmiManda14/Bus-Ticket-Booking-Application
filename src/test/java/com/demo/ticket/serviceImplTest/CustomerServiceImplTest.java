package com.demo.ticket.serviceImplTest;
 
 
import com.demo.ticket.entity.Address;
import com.demo.ticket.entity.Customer;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.AddressRepository;
import com.demo.ticket.repository.CustomerRepository;
import com.demo.ticket.serviceImpl.CustomerServiceImpl;
 
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
 
/**
 * Unit tests for CustomerServiceImpl using JUnit 5 and Mockito.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
 
    @Mock private CustomerRepository customerRepo;
    @Mock private AddressRepository addressRepo;
 
    private CustomerServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new CustomerServiceImpl(customerRepo, addressRepo);
    }
 
    // ------------------------- create(...) -------------------------
 
    @Test
    void create_shouldNormalizeAndSave_whenUniqueAndAddressExists() {
        // Arrange
        Integer addressId = 10;
        Customer input = Customer.builder()
                .name(" Alice ")
                .email("  Alice@Example.COM  ")
                .phone("  9999999999  ")
                .build();
 
        when(customerRepo.existsByEmailIgnoreCase("alice@example.com")).thenReturn(false);
        when(customerRepo.existsByPhone("9999999999")).thenReturn(false);
 
        Address addr = new Address();
        addr.setId(addressId);
        when(addressRepo.findById(addressId)).thenReturn(Optional.of(addr));
 
        when(customerRepo.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1);
            return c;
        });
 
        // Act
        Customer saved = service.create(addressId, input);
 
        // Assert
        assertNotNull(saved.getId());
        assertEquals(" Alice ", saved.getName(), "Name is not normalized in service");
        assertEquals("alice@example.com", saved.getEmail(), "Email must be trimmed + lower-cased");
        assertEquals("9999999999", saved.getPhone(), "Phone must be trimmed");
        assertSame(addr, saved.getAddress(), "Address must be set from repository");
 
        verify(customerRepo).existsByEmailIgnoreCase("alice@example.com");
        verify(customerRepo).existsByPhone("9999999999");
        verify(addressRepo).findById(addressId);
        verify(customerRepo).save(any(Customer.class));
        verifyNoMoreInteractions(customerRepo, addressRepo);
    }
 
    @Test
    void create_shouldThrow_whenDuplicateEmail() {
        // Arrange
        Customer input = Customer.builder()
                .name("Bob")
                .email("bob@example.com")
                .phone("9999999999")
                .build();
 
        when(customerRepo.existsByEmailIgnoreCase("bob@example.com")).thenReturn(true);
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.create(1, input));
 
        verify(customerRepo).existsByEmailIgnoreCase("bob@example.com");
        verifyNoMoreInteractions(customerRepo);
        verifyNoInteractions(addressRepo);
    }
 
    @Test
    void create_shouldThrow_whenDuplicatePhone() {
        // Arrange
        Customer input = Customer.builder()
                .name("Bob")
                .email("bob@example.com")
                .phone("9999999999")
                .build();
 
        when(customerRepo.existsByEmailIgnoreCase("bob@example.com")).thenReturn(false);
        when(customerRepo.existsByPhone("9999999999")).thenReturn(true);
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.create(1, input));
 
        verify(customerRepo).existsByEmailIgnoreCase("bob@example.com");
        verify(customerRepo).existsByPhone("9999999999");
        verifyNoMoreInteractions(customerRepo);
        verifyNoInteractions(addressRepo);
    }
 
    @Test
    void create_shouldThrow_whenAddressNotFound() {
        // Arrange
        Integer missingAddressId = 404;
        Customer input = Customer.builder()
                .name("Charlie")
                .email("charlie@example.com")
                .phone("8888888888")
                .build();
 
        when(customerRepo.existsByEmailIgnoreCase("charlie@example.com")).thenReturn(false);
        when(customerRepo.existsByPhone("8888888888")).thenReturn(false);
        when(addressRepo.findById(missingAddressId)).thenReturn(Optional.empty());
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class, () -> service.create(missingAddressId, input));
 
        verify(customerRepo).existsByEmailIgnoreCase("charlie@example.com");
        verify(customerRepo).existsByPhone("8888888888");
        verify(addressRepo).findById(missingAddressId);
        verifyNoMoreInteractions(customerRepo, addressRepo);
    }
 
    // ------------------------- updateLastName(...) -------------------------
 
    @Test
    void updateLastName_shouldSave_whenCustomerExists() {
        // Arrange
        Customer existing = Customer.builder().id(5).name("Old Name").build();
        when(customerRepo.findById(5)).thenReturn(Optional.of(existing));
        when(customerRepo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
 
        // Act
        Customer updated = service.updateLastName(5, "New Last");
 
        // Assert
        assertEquals("New Last", updated.getName());
        assertEquals(5, updated.getId());
 
        verify(customerRepo).findById(5);
        verify(customerRepo).save(existing);
        verifyNoMoreInteractions(customerRepo);
    }
 
    @Test
    void updateLastName_shouldThrow_whenCustomerMissing() {
        when(customerRepo.findById(9)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateLastName(9, "X"));
        verify(customerRepo).findById(9);
        verifyNoMoreInteractions(customerRepo);
    }
 
    // ------------------------- updateEmail(...) -------------------------
 
    @Test
    void updateEmail_shouldThrow_whenInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> service.updateEmail(1, "not-an-email"));
        verifyNoInteractions(customerRepo);
    }
 
    @Test
    void updateEmail_shouldThrow_whenCustomerMissing() {
        when(customerRepo.findById(7)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.updateEmail(7, "a@b.com"));
        verify(customerRepo).findById(7);
        verifyNoMoreInteractions(customerRepo);
    }
 
    @Test
    void updateEmail_shouldSave_whenValid() {
        Customer existing = Customer.builder().id(3).email("old@x.com").build();
        when(customerRepo.findById(3)).thenReturn(Optional.of(existing));
        when(customerRepo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
 
        Customer updated = service.updateEmail(3, "new@x.com");
 
        assertEquals("new@x.com", updated.getEmail());
        assertEquals(3, updated.getId());
 
        verify(customerRepo).findById(3);
        verify(customerRepo).save(existing);
        verifyNoMoreInteractions(customerRepo);
    }
 
    // ------------------------- updateCustomer(...) -------------------------
 
    @Test
    void updateCustomer_shouldApplyProvidedFields_andSave() {
        // Arrange
        Integer customerId = 20;
        Customer existing = Customer.builder()
                .id(customerId)
                .name("Old Name")
                .email("old@x.com")
                .phone("1111111111")
                .build();
 
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(existing));
        when(customerRepo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
 
        // Act
        Customer res = service.updateCustomer(
                customerId,
                "New Name",
                "new@x.com",
                "2222222222",
                null // no address change
        );
 
        // Assert
        assertEquals("New Name", res.getName());
        assertEquals("new@x.com", res.getEmail());
        assertEquals("2222222222", res.getPhone());
        assertEquals(customerId, res.getId());
 
        verify(customerRepo).findById(customerId);
        verify(customerRepo).save(existing);
        verifyNoMoreInteractions(customerRepo);
        verifyNoInteractions(addressRepo);
    }
 
    @Test
    void updateCustomer_shouldSetAddress_whenAddressIdProvided_andExists() {
        Integer customerId = 21;
        Integer addressId = 100;
 
        Customer existing = Customer.builder()
                .id(customerId)
                .name("Name")
                .build();
 
        Address address = new Address();
        address.setId(addressId);
 
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(existing));
        when(addressRepo.findById(addressId)).thenReturn(Optional.of(address));
        when(customerRepo.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
 
        Customer res = service.updateCustomer(customerId, null, null, null, addressId);
 
        assertNotNull(res.getAddress());
        assertEquals(addressId, res.getAddress().getId());
 
        verify(customerRepo).findById(customerId);
        verify(addressRepo).findById(addressId);
        verify(customerRepo).save(existing);
    }
 
    @Test
    void updateCustomer_shouldThrow_whenAddressMissing() {
        Integer customerId = 30;
        Integer missingAddressId = 404;
 
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(new Customer()));
        when(addressRepo.findById(missingAddressId)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCustomer(customerId, null, null, null, missingAddressId));
 
        verify(addressRepo).findById(missingAddressId);
        verify(customerRepo, never()).save(any());
    }
 
    @Test
    void updateCustomer_shouldThrow_whenCustomerMissing() {
        when(customerRepo.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCustomer(999, "N", "e@x.com", "1111111111", null));
        verify(customerRepo).findById(999);
        verifyNoMoreInteractions(customerRepo);
        verifyNoInteractions(addressRepo);
    }
 
    // ------------------------- get(...) -------------------------
 
    @Test
    void get_shouldReturnCustomer_whenFound() {
        Customer c = Customer.builder().id(2).name("Z").build();
        when(customerRepo.findById(2)).thenReturn(Optional.of(c));
 
        Customer res = service.get(2);
 
        assertSame(c, res);
        verify(customerRepo).findById(2);
        verifyNoMoreInteractions(customerRepo);
    }
 
    @Test
    void get_shouldThrow_whenMissing() {
        when(customerRepo.findById(3)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.get(3));
        verify(customerRepo).findById(3);
        verifyNoMoreInteractions(customerRepo);
    }
 
    // ------------------------- getAll(...) -------------------------
 
    @Test
    void getAll_shouldReturnList() {
        List<Customer> list = Arrays.asList(
                Customer.builder().id(1).name("A").build(),
                Customer.builder().id(2).name("B").build()
        );
        when(customerRepo.findAll()).thenReturn(list);
 
        List<Customer> res = service.getAll();
 
        assertEquals(2, res.size());
        assertEquals("A", res.get(0).getName());
        assertEquals("B", res.get(1).getName());
 
        verify(customerRepo).findAll();
        verifyNoMoreInteractions(customerRepo);
    }
}
 