package com.demo.ticket.serviceImplTest;
 
 
 
import com.demo.ticket.entity.*;
import com.demo.ticket.exception.ConflictException;
import com.demo.ticket.exception.ResourceNotFoundException;
import com.demo.ticket.repository.CustomerRepository;
import com.demo.ticket.repository.ReviewRepository;
import com.demo.ticket.repository.RouteRepository;
import com.demo.ticket.repository.TripRepository;
import com.demo.ticket.serviceImpl.ReviewServiceImpl;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
 
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
 
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
 
/**
 * Unit tests for ReviewServiceImpl using JUnit 5 and Mockito.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
 
    @Mock private ReviewRepository reviewRepo;
    @Mock private TripRepository tripRepo;
    @Mock private CustomerRepository customerRepo;
    @Mock private RouteRepository routeRepo;
 
    private ReviewServiceImpl service;
 
    @BeforeEach
    void setUp() {
        service = new ReviewServiceImpl(reviewRepo, tripRepo, customerRepo, routeRepo);
    }
 
    // ====================== create(...) ======================
 
   
     
 
    @Test
    void create_shouldThrowConflict_whenDuplicateForTripAndCustomer() {
        Integer tripId = 1, customerId = 2, routeId = 3;
 
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(Trip.builder().id(tripId).build()));
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(Customer.builder().id(customerId).build()));
        when(routeRepo.findById(routeId)).thenReturn(Optional.of(Route.builder().id(routeId).build()));
        when(reviewRepo.existsByTrip_IdAndCustomer_Id(tripId, customerId)).thenReturn(true);
 
        Review payload = Review.builder().rating(4).comment("dup").build();
 
        assertThrows(ConflictException.class,
                () -> service.create(tripId, customerId, routeId, payload, OffsetDateTime.now()));
 
        verify(reviewRepo).existsByTrip_IdAndCustomer_Id(tripId, customerId);
        verify(reviewRepo, never()).save(any());
    }
 
    @Test
    void create_shouldThrowNotFound_whenTripMissing() {
        Integer tripId = 404;
        when(tripRepo.findById(tripId)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(tripId, 1, 1, Review.builder().rating(3).build(), OffsetDateTime.now()));
 
        verify(tripRepo).findById(tripId);
        verifyNoMoreInteractions(tripRepo);
        verifyNoInteractions(customerRepo, routeRepo, reviewRepo);
    }
 
    @Test
    void create_shouldThrowNotFound_whenCustomerMissing() {
        Integer tripId = 1, customerId = 404;
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(Trip.builder().id(tripId).build()));
        when(customerRepo.findById(customerId)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(tripId, customerId, 1, Review.builder().rating(3).build(), OffsetDateTime.now()));
 
        verify(tripRepo).findById(tripId);
        verify(customerRepo).findById(customerId);
        verifyNoMoreInteractions(tripRepo, customerRepo);
        verifyNoInteractions(routeRepo, reviewRepo);
    }
 
    @Test
    void create_shouldThrowNotFound_whenRouteMissing() {
        Integer tripId = 1, customerId = 2, routeId = 404;
        when(tripRepo.findById(tripId)).thenReturn(Optional.of(Trip.builder().id(tripId).build()));
        when(customerRepo.findById(customerId)).thenReturn(Optional.of(Customer.builder().id(customerId).build()));
        when(routeRepo.findById(routeId)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.create(tripId, customerId, routeId, Review.builder().rating(3).build(), OffsetDateTime.now()));
 
        verify(routeRepo).findById(routeId);
        verifyNoInteractions(reviewRepo);
    }
 
 
    // ====================== byTrip / byCustomer ======================
 
    @Test
    void byTrip_shouldReturnListFromRepo() {
        Integer tripId = 9;
        List<Review> list = Arrays.asList(
                Review.builder().id(1).build(),
                Review.builder().id(2).build()
        );
        when(reviewRepo.findByTrip_Id(tripId)).thenReturn(list);
 
        List<Review> res = service.byTrip(tripId);
 
        assertEquals(2, res.size());
        assertEquals(1, res.get(0).getId());
        assertEquals(2, res.get(1).getId());
 
        verify(reviewRepo).findByTrip_Id(tripId);
        verifyNoMoreInteractions(reviewRepo);
    }
 
    @Test
    void byCustomer_shouldReturnListFromRepo() {
        Integer customerId = 7;
        List<Review> list = Arrays.asList(
                Review.builder().id(10).build(),
                Review.builder().id(11).build()
        );
        when(reviewRepo.findByCustomer_Id(customerId)).thenReturn(list);
 
        List<Review> res = service.byCustomer(customerId);
 
        assertEquals(2, res.size());
        assertEquals(10, res.get(0).getId());
        assertEquals(11, res.get(1).getId());
 
        verify(reviewRepo).findByCustomer_Id(customerId);
        verifyNoMoreInteractions(reviewRepo);
    }
 
    // ====================== byOffice / byAgency / byDriver ======================
 
    @Test
    void byOffice_shouldFilterByOfficeId() {
        Integer officeId = 77;
 
        Review r1 = reviewWithOffice(1, officeId);
        Review r2 = reviewWithOffice(2, 88);
        Review r3_missingChain = Review.builder().id(3)
                .trip(Trip.builder().id(100).bus(null).build())
                .build();
 
        when(reviewRepo.findAll()).thenReturn(Arrays.asList(r1, r2, r3_missingChain));
 
        List<Review> res = service.byOffice(officeId);
 
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).getId());
 
        verify(reviewRepo).findAll();
    }
 
    @Test
    void byAgency_shouldFilterByAgencyId() {
        Integer agencyId = 100;
 
        Review r1 = reviewWithAgency(1, agencyId);
        Review r2 = reviewWithAgency(2, 200);
        Review r3_missingChain = Review.builder().id(3)
                .trip(Trip.builder().id(300).bus(null).build())
                .build();
 
        when(reviewRepo.findAll()).thenReturn(Arrays.asList(r1, r2, r3_missingChain));
 
        List<Review> res = service.byAgency(agencyId);
 
        assertEquals(1, res.size());
        assertEquals(1, res.get(0).getId());
 
        verify(reviewRepo).findAll();
    }
 
    @Test
    void byDriver_shouldFilterWhenDriver1OrDriver2Matches() {
        Integer driverId = 501;
 
        Driver d1 = Driver.builder().id(driverId).build();
        Driver d2 = Driver.builder().id(999).build();
 
        Review r1 = Review.builder()
                .id(1)
                .trip(Trip.builder().id(100).driver1(d1).driver2(null).build())
                .build();
 
        Review r2 = Review.builder()
                .id(2)
                .trip(Trip.builder().id(101).driver1(null).driver2(d1).build())
                .build();
 
        Review r3 = Review.builder()
                .id(3)
                .trip(Trip.builder().id(102).driver1(null).driver2(d2).build())
                .build();
 
        when(reviewRepo.findAll()).thenReturn(Arrays.asList(r1, r2, r3));
 
        List<Review> res = service.byDriver(driverId);
 
        assertEquals(2, res.size());
        assertEquals(1, res.get(0).getId());
        assertEquals(2, res.get(1).getId());
 
        verify(reviewRepo).findAll();
    }
 
    // ====================== update(...) ======================
 
    @Test
    void update_shouldApplyProvidedFields_andSave() {
        Integer reviewId = 50;
        Review existing = Review.builder()
                .id(reviewId)
                .trip(Trip.builder().id(1).build())
                .customer(Customer.builder().id(2).build())
                .rating(3)
                .comment("old")
                .build();
 
        when(reviewRepo.findById(reviewId)).thenReturn(Optional.of(existing));
 
        Trip newTrip = Trip.builder().id(10).build();
        Customer newCustomer = Customer.builder().id(20).build();
 
        when(tripRepo.findById(10)).thenReturn(Optional.of(newTrip));
        when(customerRepo.findById(20)).thenReturn(Optional.of(newCustomer));
        when(reviewRepo.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
 
        Review updated = service.update(reviewId, 10, 20, 5, "updated");
 
        assertEquals(reviewId, updated.getId());
        assertSame(newTrip, updated.getTrip());
        assertSame(newCustomer, updated.getCustomer());
        assertEquals(5, updated.getRating());
        assertEquals("updated", updated.getComment());
 
        verify(reviewRepo).findById(reviewId);
        verify(tripRepo).findById(10);
        verify(customerRepo).findById(20);
        verify(reviewRepo).save(existing); // same instance updated & saved
    }
 
    @Test
    void update_shouldThrow_whenReviewMissing() {
        when(reviewRepo.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(999, null, null, null, null));
        verify(reviewRepo).findById(999);
        verifyNoMoreInteractions(reviewRepo);
    }
 
    @Test
    void update_shouldThrow_whenTripToSetMissing() {
        Review existing = Review.builder().id(1).build();
        when(reviewRepo.findById(1)).thenReturn(Optional.of(existing));
        when(tripRepo.findById(404)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1, 404, null, null, null));
 
        verify(tripRepo).findById(404);
        verify(reviewRepo, never()).save(any());
    }
 
    @Test
    void update_shouldThrow_whenCustomerToSetMissing() {
        Review existing = Review.builder().id(1).build();
        when(reviewRepo.findById(1)).thenReturn(Optional.of(existing));
        when(customerRepo.findById(404)).thenReturn(Optional.empty());
 
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1, null, 404, null, null));
 
        verify(customerRepo).findById(404);
        verify(reviewRepo, never()).save(any());
    }
 
    @Test
    void update_shouldThrow_whenRatingOutOfRange() {
        Review existing = Review.builder().id(1).build();
        when(reviewRepo.findById(1)).thenReturn(Optional.of(existing));
 
        assertThrows(IllegalArgumentException.class,
                () -> service.update(1, null, null, 0, null));  // < 1
        assertThrows(IllegalArgumentException.class,
                () -> service.update(1, null, null, 6, null));  // > 5
 
        verify(reviewRepo, never()).save(any());
    }
 
    // ---------------------- helpers ----------------------
 
    private Review reviewWithOffice(int reviewId, int officeId) {
        AgencyOffice office = AgencyOffice.builder().id(officeId).build();
        Bus bus = Bus.builder().id(600).office(office).build();
        Trip trip = Trip.builder().id(500).bus(bus).build();
        return Review.builder().id(reviewId).trip(trip).build();
    }
 
    private Review reviewWithAgency(int reviewId, int agencyId) {
        Agency agency = Agency.builder().id(agencyId).build();
        AgencyOffice office = AgencyOffice.builder().id(700).agency(agency).build();
        Bus bus = Bus.builder().id(600).office(office).build();
        Trip trip = Trip.builder().id(500).bus(bus).build();
        return Review.builder().id(reviewId).trip(trip).build();
    }
}
 
