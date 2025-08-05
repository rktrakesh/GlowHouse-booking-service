package com.glowhouse.controller;

import com.glowhouse.domen.BookingStatus;
import com.glowhouse.dto.*;
import com.glowhouse.entity.Booking;
import com.glowhouse.mapper.BookingMapper;
import com.glowhouse.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/booking")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    @PostMapping("/addNewBookings")
    public ResponseEntity<?> addNewBookings(@RequestParam Long salonId,
                                            @RequestBody BookingDTO bookingDto) {

        try {
            if (salonId == null || bookingDto == null) {
                logger.warn("Provided fields must not be null.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Provided fields must not be null.");
            }
            // demo details
            logger.info("addNewBookings:: starts.");
            UserDTO userDto = new UserDTO();
            userDto.setId(1L);

            SaloonDTO saloonDTO = new SaloonDTO();
            saloonDTO.setId(salonId);
            saloonDTO.setOpenTime(LocalDateTime.now());
            saloonDTO.setCloseTime(LocalDateTime.now().plusHours(12));

            Set<ServiceOfferingDto> offeringDto = new HashSet<>();
            ServiceOfferingDto serviceOfferingDto = new ServiceOfferingDto();
            serviceOfferingDto.setId(1L);
            serviceOfferingDto.setPrice(599);
            serviceOfferingDto.setDuration(60);
            serviceOfferingDto.setServiceName("Hair Cut");
            offeringDto.add(serviceOfferingDto);

            Booking booking = bookingService.addNewBooking(bookingDto, userDto, saloonDTO, offeringDto);
            if (booking != null) {
                BookingResponse response = BookingMapper.mapToDto(booking);
                logger.info("addNewBookings:: ends.");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(response);
            }

            logger.warn("Unable to add booking details for salonId: {}", saloonDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to add booking details for salonId: " + salonId);
        } catch (Exception e) {
            logger.error("Exception while adding new booking details: {}", e.getMessage());
            ErrorResponse response = new ErrorResponse();
            response.setMessage("Exception while adding new booking details.");
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/getBookingsByCustomerId")
    public ResponseEntity<?> getBookingsByCustomerId() {
        try {
            logger.info("getBookingsByCustomerId:: starts.");
            //Demo data
            UserDTO userDTO = new UserDTO();
            userDTO.setId(1L);
            List<Booking> bookingsByCustomer = bookingService.getBookingsByCustomer(userDTO.getId());
            if (bookingsByCustomer != null) {
                Set<BookingResponse> bookingResponses = bookingsByCustomer.stream()
                        .map(BookingMapper::mapToDto)
                        .collect(Collectors.toSet());
                logger.info("getBookingsByCustomerId:: ends.");
                return ResponseEntity.ok(bookingResponses);
            }
            logger.warn("Unable to find booking details for customerId: {}", userDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Unable to find booking details for customerId: " + userDTO.getId());
        } catch (Exception e) {
            logger.error("Exception while getting booking details: {}", e.getMessage());
            ErrorResponse response = new ErrorResponse();
            response.setMessage("Exception while getting booking details.");
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/getDetailBySalonId/{id}")
    public ResponseEntity<?> getBookingsBySalonId(@PathVariable("id") Long salonId) {
        try {
            logger.info("getBookingsBySalonId: Starts.");
            if (salonId == null) {
                logger.warn("Salon id not provided.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("SalonId must not be null, please provide salonId.");
            }
            List<Booking> bookingsDetails = bookingService.getBookingsBySalonId(salonId);
            if (bookingsDetails != null && !bookingsDetails.isEmpty()) {
                Set<BookingResponse> bookingResponses = bookingsDetails.stream()
                        .map(BookingMapper::mapToDto)
                        .collect(Collectors.toSet());
                logger.info("getBookingsBySalonId: Ends.");
                return ResponseEntity.ok(bookingResponses);
            }
            logger.warn("There are no bookings founds for salonId: {}", salonId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no bookings founds for salonId: " + salonId);
        } catch (Exception e) {
            logger.error("Exception while getting booking details by ownerId: {}", e.getMessage());
            ErrorResponse response = new ErrorResponse();
            response.setMessage("Exception while getting booking details by salonId: " + salonId);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/getBookingsByid/{id}")
    public ResponseEntity<?> getBookingsByBookingId (@PathVariable ("id") Long bookingId) {
        try {
            logger.info("getBookingsByBookingId: Starts.");
            if (bookingId == null) {
                logger.warn("booking id not provided.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("bookingId must not be null, please provide bookingId.");
            }
            Booking booking = bookingService.getBookingById(bookingId);
            if (booking != null) {
                BookingResponse response = BookingMapper.mapToDto(booking);
                logger.info("getBookingsByBookingId: Ends.");
                return ResponseEntity.ok(response);
            }
            logger.warn("There are no bookings founds for bookingId: {}", bookingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no bookings founds for bookingId: " + bookingId);
        } catch (Exception e) {
            logger.error("Exception while getting booking details by bookingId: {}", e.getMessage());
            ErrorResponse response = new ErrorResponse();
            response.setMessage("Exception while getting booking details by bookingId: " + bookingId);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PostMapping("/changeBookingStatus/{id}")
    public ResponseEntity<?> changeBookingStatus (@PathVariable ("id") Long bookingId, @RequestParam BookingStatus bookingStatus) {
        try {
            logger.info("changeBookingStatus:: Starts.");
            if (bookingId == null) {
                logger.warn("booking id must not be null.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Booking id must not be null, Please provide Booing id.");
            }
            Booking booking = bookingService.changeBookingStatus(bookingId, bookingStatus);
            if (booking != null) {
                BookingResponse response = BookingMapper.mapToDto(booking);
                logger.info("changeBookingStatus: Ends.");
                return ResponseEntity.ok(response);
            }
            logger.warn("Unable to change the status for bookingId: {}", bookingId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to change the status for bookingId: " + bookingId);
        } catch (Exception e) {
            logger.error("Exception while changing booking status by bookingId: {}", e.getMessage());
            ErrorResponse response = new ErrorResponse();
            response.setMessage("Exception while changing booking status by bookingId: " + bookingId);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/slots/salon/{salonid}/date/{date}")
    public ResponseEntity<?> getBookingsByDate (@RequestParam Long salonId, @RequestParam LocalDateTime date) {
        try {
            logger.info("getBookingsByDate:: Starts.");
            List<Booking> bookingsByDate = bookingService.getBookingsByDate(date, salonId);
            if(bookingsByDate != null) {
                List<BookingSlotDto> bookingSlotDtoList = bookingsByDate.stream()
                        .map(booking -> {
                            BookingSlotDto bookingSlotDto = new BookingSlotDto();
                            bookingSlotDto.setStartTime(booking.getStartTime());
                            bookingSlotDto.setEndTime(booking.getEndTime());
                            return bookingSlotDto;
                        }).toList();
                logger.info("getBookingsByDate:: Ends.");
                return ResponseEntity.ok(bookingSlotDtoList);
            }
            logger.warn("Unable to get Bookings details for date: {}", date);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Unable to get Bookings details for date: " + date);
        } catch (Exception e) {
            logger.error("Exception while getting bookings by date: {}", e.getMessage());
            ErrorResponse response = new ErrorResponse();
            response.setMessage("Exception while getting bookings by date: " + date);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @GetMapping("/report")
    public ResponseEntity<?> getSalonReport () {
        try {
            logger.info("getSalonReport:: Starts.");
            return ResponseEntity.ok(bookingService.getSalonReport(1L));
        } catch (Exception e) {
            logger.error("Exception while getting salon report: {}", e.getMessage());
            ErrorResponse response = new ErrorResponse();
            response.setMessage("Exception while getting salon report.");
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

}