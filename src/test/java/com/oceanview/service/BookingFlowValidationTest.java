package com.oceanview.service;

import com.oceanview.dao.IGuestReservationGateway;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class BookingFlowValidationTest {

    private BookingFlowOrchestratorImpl bookingService;

    @Mock
    private IGuestReservationGateway
            mockReservationGateway;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService =
                new BookingFlowOrchestratorImpl(
                        mockReservationGateway);
    }

    // ===== GUEST NAME =====

    @Test
    public void testValidGuestNameAccepted() {
        assertTrue(bookingService
                .isValidGuestName("Kasun Perera"));
    }

    @Test
    public void testEmptyGuestNameRejected() {
        assertFalse(bookingService
                .isValidGuestName(""));
    }

    @Test
    public void testNullGuestNameRejected() {
        assertFalse(bookingService
                .isValidGuestName(null));
    }

    @Test
    public void testNumericGuestNameRejected() {
        assertFalse(bookingService
                .isValidGuestName("John123"));
    }

    @Test
    public void testSingleCharNameRejected() {
        assertFalse(bookingService
                .isValidGuestName("A"));
    }

    // ===== CONTACT NUMBER =====

    @Test
    public void testValidContactAccepted() {
        assertTrue(bookingService
                .isValidContactNumber("0771234567"));
    }

    @Test
    public void testShortContactRejected() {
        assertFalse(bookingService
                .isValidContactNumber("077123"));
    }

    @Test
    public void testContactNotStarting0Rejected() {
        assertFalse(bookingService
                .isValidContactNumber("1234567890"));
    }

    @Test
    public void testNullContactRejected() {
        assertFalse(bookingService
                .isValidContactNumber(null));
    }

    // ===== DATES =====

    @Test
    public void testValidDatesAccepted() {
        assertTrue(bookingService.areValidDates(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(4)));
    }

    @Test
    public void testPastCheckInRejected() {
        assertFalse(bookingService.areValidDates(
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(2)));
    }

    @Test
    public void testCheckOutBeforeCheckInRejected() {
        assertFalse(bookingService.areValidDates(
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2)));
    }

    @Test
    public void testNullDatesRejected() {
        assertFalse(bookingService.areValidDates(
                null, LocalDate.now().plusDays(1)));
    }

    @Test
    public void testSameDayRejected() {
        LocalDate sameDay = LocalDate.now().plusDays(1);
        assertFalse(bookingService
                .areValidDates(sameDay, sameDay));
    }
}