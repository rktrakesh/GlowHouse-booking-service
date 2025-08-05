package com.glowhouse.service;

import com.glowhouse.domen.BookingStatus;
import com.glowhouse.dto.*;
import com.glowhouse.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingService {

    Booking addNewBooking (BookingDTO bookingDTO,
                           UserDTO userDto,
                           SaloonDTO saloonDto,
                           Set<ServiceOfferingDto> serviceDto);

    List<Booking> getBookingsByCustomer (Long customerId);

    List<Booking> getBookingsBySalonId (Long salonId);

    Booking getBookingById (Long booingId);

    Booking changeBookingStatus (Long bookingId, BookingStatus status);

    List<Booking> getBookingsByDate (LocalDateTime date, Long salonId);

    SalonReport getSalonReport (Long salonId);

}
