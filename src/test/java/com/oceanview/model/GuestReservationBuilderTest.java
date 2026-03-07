package com.oceanview.model;

import org.junit.Test;
import java.time.LocalDate;
import static org.junit.Assert.*;

public class GuestReservationBuilderTest {

    @Test
    public void testBuilderCreatesObjectWithAllFields() {
        GuestReservation reservation =
                new GuestReservation.Builder()
                        .reservationId(1)
                        .reservationNumber("RES-2026-00001")
                        .guestName("Kasun Perera")
                        .address("Colombo")
                        .contactNumber("0771234567")
                        .guestEmail("kasun@gmail.com")
                        .roomId(1)
                        .roomType("Standard")
                        .checkInDate(LocalDate.of(
                                2026, 9, 1))
                        .checkOutDate(LocalDate.of(
                                2026, 9, 4))
                        .numberOfNights(3)
                        .createdBy(1)
                        .build();

        assertEquals("RES-2026-00001",
                reservation.getReservationNumber());
        assertEquals("Kasun Perera",
                reservation.getGuestName());
        assertEquals("Colombo",
                reservation.getAddress());
        assertEquals("0771234567",
                reservation.getContactNumber());
        assertEquals("kasun@gmail.com",
                reservation.getGuestEmail());
        assertEquals(1, reservation.getRoomId());
        assertEquals("Standard",
                reservation.getRoomType());
        assertEquals(3,
                reservation.getNumberOfNights());
    }

    @Test
    public void testBuilderDefaultStatus() {
        GuestReservation reservation =
                new GuestReservation.Builder()
                        .guestName("Test Guest")
                        .build();

        assertEquals("Default status should be "
                + "Confirmed", "Confirmed",
                reservation.getReservationStatus());
    }

    @Test
    public void testBuilderDatesCorrect() {
        LocalDate checkIn = LocalDate.of(2026, 10, 1);
        LocalDate checkOut = LocalDate.of(2026, 10, 5);

        GuestReservation reservation =
                new GuestReservation.Builder()
                        .checkInDate(checkIn)
                        .checkOutDate(checkOut)
                        .build();

        assertEquals(checkIn,
                reservation.getCheckInDate());
        assertEquals(checkOut,
                reservation.getCheckOutDate());
    }

    @Test
    public void testInvoiceRecordBuilder() {
        InvoiceRecord bill =
                new InvoiceRecord.Builder()
                        .billId(1)
                        .billNumber("BILL-2026-00001")
                        .reservationId(1)
                        .guestName("Kasun Perera")
                        .roomType("Standard")
                        .ratePerNight(5500.00)
                        .numberOfNights(3)
                        .subtotal(16500.00)
                        .taxAmount(0.00)
                        .totalAmount(16500.00)
                        .build();

        assertEquals("BILL-2026-00001",
                bill.getBillNumber());
        assertEquals(5500.00,
                bill.getRatePerNight(), 0.01);
        assertEquals(16500.00,
                bill.getTotalAmount(), 0.01);
        assertEquals(3, bill.getNumberOfNights());
    }

    @Test
    public void testSystemUserBuilder() {
        SystemUser user = new SystemUser.Builder()
                .userId(1)
                .username("admin")
                .fullName("Resort Manager")
                .userRole("ADMIN")
                .isActive(true)
                .mustChangePassword(false)
                .build();

        assertEquals("admin", user.getUsername());
        assertEquals("ADMIN", user.getUserRole());
        assertTrue(user.getIsActive());
        assertFalse(user.getMustChangePassword());
    }

    @Test
    public void testResortRoomBuilder() {
        ResortRoom room = new ResortRoom.Builder()
                .roomId(1)
                .roomNumber("101")
                .roomType("Standard")
                .ratePerNight(5500.00)
                .isAvailable(true)
                .floorNumber(1)
                .maxGuests(2)
                .build();

        assertEquals("101", room.getRoomNumber());
        assertEquals(5500.00,
                room.getRatePerNight(), 0.01);
        assertTrue(room.getIsAvailable());
        assertEquals(2, room.getMaxGuests());
    }
}