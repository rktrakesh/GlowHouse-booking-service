package com.glowhouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@AllArgsConstructor
public class SalonReport {

    private Long salonId;
    private String salonName;
    private Double totalEarning;
    private Long totalBooking;
    private Long cancelBooking;
    private Double totalRefund;

}
