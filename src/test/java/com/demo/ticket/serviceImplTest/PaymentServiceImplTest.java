package com.demo.ticket.serviceImplTest;
 
 
 
import com.demo.ticket.entity.*;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.BookingRepository;
import com.demo.ticket.repository.CustomerRepository;
import com.demo.ticket.repository.PaymentRepository;
import com.demo.ticket.serviceImpl.PaymentServiceImpl;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
 
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
 
    @Mock private PaymentRepository paymentRepo;
    @Mock private BookingRepository bookingRepo;
    @Mock private CustomerRepository customerRepo;
 
    private PaymentServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new PaymentServiceImpl(paymentRepo, bookingRepo, customerRepo);
    }
 
    // ======================= create(...) =======================
 
    @Test
    void create_shouldSave_whenBookingAndCustomerExist_andNoDuplicate() {
        // Arrange
        Integer bookingId = 10;
        Integer customerId = 20;
 
        Booking booking = Booking.builder().id(bookingId).build();
        Customer customer = Customer.builder().id(customerId).build();
 
        when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(booking));
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(customer));
        when(paymentRepo.existsByBooking_Id(bookingId)).thenReturn(false);
 
        Payment toSave = Payment.builder()
                .amount(new BigDecimal("123.45"))
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();
 
        when(paymentRepo.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setId(1);
            return p;
        });
 
        // Act
        Payment saved = service.create(bookingId, customerId, toSave);
 
        // Assert
        assertNotNull(saved.getId());
        assertEquals(new BigDecimal("123.45"), saved.getAmount());
        assertEquals(PaymentStatus.SUCCESS, saved.getPaymentStatus());
        assertSame(booking, saved.getBooking(), "Service must set booking on payment");
        assertSame(customer, saved.getCustomer(), "Service must set customer on payment");
 
        verify(bookingRepo).findById(bookingId);
        verify(customerRepo).findById(customerId);
        verify(paymentRepo).existsByBooking_Id(bookingId);
        verify(paymentRepo).save(toSave);
        verifyNoMoreInteractions(paymentRepo, bookingRepo, customerRepo);
    }
 
    @Test
    void create_shouldThrowConflict_whenPaymentAlreadyExistsForBooking() {
        // Arrange
        Integer bookingId = 10;
        Integer customerId = 20;
 
        when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(Booking.builder().id(bookingId).build()));
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(Customer.builder().id(customerId).build()));
        when(paymentRepo.existsByBooking_Id(bookingId)).thenReturn(true);
 
        Payment toSave = Payment.builder()
                .amount(new BigDecimal("50.00"))
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();
 
        // Act
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.create(bookingId, customerId, toSave));
 
        // Assert
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode(),
                "Should return 409 Conflict when booking already has a payment");
        assertTrue(ex.getReason() != null && ex.getReason().toLowerCase().contains("already exists"),
                "Reason should mention duplicate payment for booking");
 
        verify(bookingRepo).findById(bookingId);
        verify(customerRepo).findById(customerId);
        verify(paymentRepo).existsByBooking_Id(bookingId);
        verify(paymentRepo, never()).save(any());
    }
 
    @Test
    void create_shouldThrowNotFound_whenBookingMissing() {
        // Arrange
        Integer bookingId = 404;
        Integer customerId = 1;
        when(bookingRepo.findById(bookingId)).thenReturn(Optional.empty());
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(bookingId, customerId, Payment.builder().build()));
 
        verify(bookingRepo).findById(bookingId);
        verifyNoMoreInteractions(bookingRepo);
        verifyNoInteractions(customerRepo, paymentRepo);
    }
 
    @Test
    void create_shouldThrowNotFound_whenCustomerMissing() {
        // Arrange
        Integer bookingId = 1;
        Integer customerId = 404;
        when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(Booking.builder().id(bookingId).build()));
        when(customerRepo.findById(customerId)).thenReturn(Optional.empty());
 
        // Act + Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(bookingId, customerId, Payment.builder().build()));
 
        verify(bookingRepo).findById(bookingId);
        verify(customerRepo).findById(customerId);
        verifyNoMoreInteractions(bookingRepo, customerRepo);
        verifyNoInteractions(paymentRepo);
    }
 
    // ======================= byCustomer(...) =======================
 
    @Test
    void byCustomer_shouldReturnListFromRepo() {
        // Arrange
        Integer customerId = 7;
        List<Payment> list = Arrays.asList(
                Payment.builder().id(1).build(),
                Payment.builder().id(2).build()
        );
        when(paymentRepo.findByCustomer_Id(customerId)).thenReturn(list);
 
        // Act
        List<Payment> result = service.byCustomer(customerId);
 
        // Assert
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
 
        verify(paymentRepo).findByCustomer_Id(customerId);
        verifyNoMoreInteractions(paymentRepo);
        verifyNoInteractions(bookingRepo, customerRepo);
    }
 
    // ======================= byBooking(...) =======================
 
    @Test
    void byBooking_shouldReturnListFromRepo() {
        // Arrange
        Integer bookingId = 5;
        List<Payment> list = Arrays.asList(
                Payment.builder().id(10).build(),
                Payment.builder().id(11).build()
        );
        when(paymentRepo.findByBooking_Id(bookingId)).thenReturn(list);
 
        // Act
        List<Payment> result = service.byBooking(bookingId);
 
        // Assert
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals(11, result.get(1).getId());
 
        verify(paymentRepo).findByBooking_Id(bookingId);
        verifyNoMoreInteractions(paymentRepo);
        verifyNoInteractions(bookingRepo, customerRepo);
    }
 
    // ======================= byAgency(...) =======================
    // Filters paymentRepo.findAll() by nested path:
    // Payment -> Booking -> Trip -> Bus -> AgencyOffice -> Agency(id)
    @Test
    void byAgency_shouldFilterPaymentsByAgencyId() {
        // Arrange
        Integer targetAgencyId = 100;
 
        Payment withTargetAgency = paymentWithAgency(1, targetAgencyId);
        Payment withOtherAgency  = paymentWithAgency(2, 200);
        Payment withoutTripChain = Payment.builder()
                .id(3)
                .booking(Booking.builder().id(30).trip(null).build())
                .build();
 
        when(paymentRepo.findAll()).thenReturn(Arrays.asList(withTargetAgency, withOtherAgency, withoutTripChain));
 
        // Act
        List<Payment> result = service.byAgency(targetAgencyId);
 
        // Assert
        assertEquals(1, result.size(), "Only payments whose booking.trip.bus.office.agency.id equals target should pass");
        assertEquals(1, result.get(0).getId());
 
        verify(paymentRepo).findAll();
        verifyNoMoreInteractions(paymentRepo);
        verifyNoInteractions(bookingRepo, customerRepo);
    }
 
    // ======================= byOffice(...) =======================
    // Filters paymentRepo.findAll() by:
    // Payment -> Booking -> Trip -> Bus -> AgencyOffice(id)
    @Test
    void byOffice_shouldFilterPaymentsByOfficeId() {
        // Arrange
        Integer targetOfficeId = 77;
 
        Payment withTargetOffice = paymentWithOffice(10, targetOfficeId);
        Payment withOtherOffice  = paymentWithOffice(11, 88);
        Payment missingChain     = Payment.builder()
                .id(12)
                .booking(Booking.builder().id(101).trip(null).build())
                .build();
 
        when(paymentRepo.findAll()).thenReturn(Arrays.asList(withTargetOffice, withOtherOffice, missingChain));
 
        // Act
        List<Payment> result = service.byOffice(targetOfficeId);
 
        // Assert
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
 
        verify(paymentRepo).findAll();
        verifyNoMoreInteractions(paymentRepo);
        verifyNoInteractions(bookingRepo, customerRepo);
    }
 
    // ---------------------- helpers to build nested graph ----------------------
 
    private Payment paymentWithAgency(int paymentId, int agencyId) {
        Agency agency = Agency.builder().id(agencyId).build();
        AgencyOffice office = AgencyOffice.builder().id(700).agency(agency).build();
        Bus bus = Bus.builder().id(600).office(office).build();
        Trip trip = Trip.builder().id(500).bus(bus).build();
        Booking booking = Booking.builder().id(400).trip(trip).build();
 
        return Payment.builder()
                .id(paymentId)
                .booking(booking)
                .customer(Customer.builder().id(200).build())
                .amount(new BigDecimal("10.00"))
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();
    }
 
    private Payment paymentWithOffice(int paymentId, int officeId) {
        AgencyOffice office = AgencyOffice.builder().id(officeId).build();
        Bus bus = Bus.builder().id(600).office(office).build();
        Trip trip = Trip.builder().id(500).bus(bus).build();
        Booking booking = Booking.builder().id(400).trip(trip).build();
 
        return Payment.builder()
                .id(paymentId)
                .booking(booking)
                .customer(Customer.builder().id(200).build())
                .amount(new BigDecimal("20.00"))
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.SUCCESS)
                .build();
    }
}
 
