package com.glowhouse.dto;

import com.glowhouse.domen.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDTO {

    @Schema(hidden = true)
    private Long id;
    @Schema(hidden = true)
    private Long salonId;
    @Schema(hidden = true)
    private Long customerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Schema(hidden = true)
    private Set<Long> serviceIds;
    @Schema(hidden = true)
    private double totalPrice;
    @Schema(hidden = true)
    private BookingStatus status = BookingStatus.PENDING;

}
