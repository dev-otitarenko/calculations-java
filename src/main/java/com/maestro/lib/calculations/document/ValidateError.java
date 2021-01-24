package com.maestro.lib.calculations.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class ValidateError {
    private int status; // Validate Status: 1 - Ok, 0 - Bad validation
    private String message; // Validate message if status == 0
}
