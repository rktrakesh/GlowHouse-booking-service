package com.glowhouse.dto;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private String message;
}
