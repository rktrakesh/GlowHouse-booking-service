package com.glowhouse.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@AllArgsConstructor
public class ServiceOfferingDto {
    @Schema(hidden = true)
    private Long id;
    private String serviceName;
    private String description;
    private int price;
    private int duration;
    @Schema(hidden = true)
    private Long salonId;
    @Schema(hidden = true)
    private Long categoryId;
    private String image;
}
