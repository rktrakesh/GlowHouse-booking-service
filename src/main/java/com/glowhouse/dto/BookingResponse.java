package com.glowhouse.dto;

import com.glowhouse.domen.BookingStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingResponse {

    private Long id;
    private Long salonId;
    private Long customerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Set<Long> serviceIds;
    private int totalPrice;
    private BookingStatus status = BookingStatus.PENDING;

}
