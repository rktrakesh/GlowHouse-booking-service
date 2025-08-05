package com.glowhouse.mapper;

import com.glowhouse.dto.BookingResponse;
import com.glowhouse.entity.Booking;

public class BookingMapper {

    public static BookingResponse mapToDto (Booking booking) {
        BookingResponse bookingDTO = new BookingResponse();
        bookingDTO.setId(booking.getId());
        bookingDTO.setStatus(booking.getStatus());
        bookingDTO.setCustomerId(booking.getCustomerId());
        bookingDTO.setEndTime(booking.getEndTime());
        bookingDTO.setSalonId(booking.getSalonId());
        bookingDTO.setServiceIds(booking.getServiceIds());
        bookingDTO.setTotalPrice(booking.getTotalPrice());
        bookingDTO.setStartTime(booking.getStartTime());
        return bookingDTO;
    }

}
