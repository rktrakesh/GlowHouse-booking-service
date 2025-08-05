package com.glowhouse.service.impl;

import com.glowhouse.domen.BookingStatus;
import com.glowhouse.dto.*;
import com.glowhouse.entity.Booking;
import com.glowhouse.repository.BookingRepo;
import com.glowhouse.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepo bookingRepo;

    @Override
    public Booking addNewBooking(BookingDTO bookingDTO,
                                 UserDTO userDto,
                                 SaloonDTO saloonDto,
                                 Set<ServiceOfferingDto> serviceDto) {
        try {
            int totalDuration = serviceDto.stream()
                    .mapToInt(ServiceOfferingDto::getDuration)
                    .sum();
            LocalDateTime bookingStartTime = bookingDTO.getStartTime();
            LocalDateTime bookingEndTime = bookingStartTime.plusMinutes(totalDuration);
            boolean timeSlotAvailable = isTimeSlotAvailable(saloonDto, bookingStartTime, bookingEndTime);
            if (!timeSlotAvailable) {
                logger.warn("Slot not available.");
                return null;
            }
            int totalAmount = serviceDto.stream()
                    .mapToInt(ServiceOfferingDto::getPrice)
                    .sum();
            Set<Long> servicesIds = serviceDto.stream()
                    .map(ServiceOfferingDto::getId)
                    .collect(Collectors.toSet());

            Booking newBooking = new Booking();
            newBooking.setStartTime(bookingStartTime);
            newBooking.setEndTime(bookingEndTime);
            newBooking.setServiceIds(servicesIds);
            newBooking.setCustomerId(userDto.getId());
            newBooking.setSalonId(saloonDto.getId());
            newBooking.setTotalPrice(totalAmount);
            return bookingRepo.save(newBooking);
        } catch (Exception e) {
            logger.error("Exception while adding new booking details: {}", e.getMessage());
            return null;
        }
    }

    private boolean isTimeSlotAvailable (SaloonDTO saloonDTO,
                                        LocalDateTime bookingStartTime,
                                        LocalDateTime bookingEndTime) {

        List<Booking> bookingLists = getBookingsBySalonId(saloonDTO.getId());
        if (bookingStartTime.isBefore(saloonDTO.getOpenTime())
                || bookingEndTime.isAfter(saloonDTO.getCloseTime())) {
            logger.warn("Booking time must be within salon working hour.");
            return false;
        }
        for (Booking booking : bookingLists) {
            LocalDateTime existingBookingStartTime = booking.getStartTime();
            LocalDateTime existingBookingEndTime = booking.getEndTime();
            if (bookingStartTime.isBefore(existingBookingEndTime) && bookingEndTime.isAfter(existingBookingStartTime)) {
                logger.warn("Slot for appointment is not available for this time.");
                return false;
            }
            if (bookingStartTime.isEqual(existingBookingStartTime)) {
                logger.warn("Slot for appointment is not available for this time. ");
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Booking> getBookingsByCustomer(Long customerId) {
        try {
            return bookingRepo.findByCustomerId(customerId);
        } catch (Exception e) {
            logger.error("Exception while getting bookings by customers: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Booking> getBookingsBySalonId(Long salonId) {
        try {
            return bookingRepo.findBySalonId(salonId);
        } catch (Exception e) {
            logger.error("Exception while getting bookings by salon Id: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Booking getBookingById(Long booingId) {
        try {
            return bookingRepo.findById(booingId).orElse(null);
        } catch (Exception e) {
            logger.error("Exception while getting bookings by booking Id: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Booking changeBookingStatus(Long bookingId, BookingStatus status) {
        try {
            Booking booking = getBookingById(bookingId);
            if (booking == null) {
                logger.warn("No bookings found under bookingId: {}", bookingId);
                return null;
            }
            booking.setStatus(status);
            return bookingRepo.save(booking);
        } catch (Exception e) {
            logger.error("Exception while changing the booking status: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public List<Booking> getBookingsByDate(LocalDateTime date, Long salonId) {
        try {
            List<Booking> bookings = getBookingsBySalonId(salonId);
            if (date == null) {
                return bookings;
            }
            return bookings.stream()
                    .filter(booking ->
                            isSameDate(booking.getStartTime(), date) || isSameDate(booking.getEndTime(), date))
                    .toList();
        } catch (Exception e) {
            logger.error("Exception while getting bookings by date: {}", e.getMessage());
            return null;
        }
    }

    private boolean isSameDate (LocalDateTime time, LocalDateTime givenTime) {
        return time.isEqual(givenTime);
    }

    @Override
    public SalonReport getSalonReport(Long salonId) {
        try {
            List<Booking> totalBookingsData = getBookingsBySalonId(salonId);
            if (totalBookingsData == null || totalBookingsData.isEmpty()) {
                logger.warn("No details are found.");
                return null;
            }
            Long totalBookings = (long) totalBookingsData.size();
            List<Booking> canceledList = totalBookingsData.stream()
                    .filter(booking ->
                            booking.getStatus().equals(BookingStatus.CANCELLED))
                    .toList();
            double totalEarning = totalBookingsData.stream()
                    .mapToDouble(Booking::getTotalPrice)
                    .sum();
            double totalRefund = canceledList.stream()
                    .mapToDouble(Booking::getTotalPrice)
                    .sum();

            SalonReport report = new SalonReport();
            report.setCancelBooking((long) canceledList.size());
            report.setTotalEarning(totalEarning);
            report.setTotalRefund(totalRefund);
            report.setTotalBooking(totalBookings);
            report.setSalonId(salonId);
            return report;
        } catch (Exception e) {
            logger.error("Exception while getting total report: {}", e.getMessage());
            return null;
        }
    }
}
