package com.maestro.lib.calculations.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
* Specific validation rule
 */
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ValidateRule {
    private String field; // field name
    private String sign; // sign
    private String expression; // expression
    private boolean onlyFilling = true;
    private boolean onlyChecking = true;
}
