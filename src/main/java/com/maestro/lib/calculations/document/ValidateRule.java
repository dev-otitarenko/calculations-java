package com.maestro.lib.calculations.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Specific validation rule
 */
public class ValidateRule {
    /**
     * Field name
     */
    private String field;
    /**
     * Sign: =, >, <, >=, <=, <>, !=
     **/
    private String sign;
    /**
     * Expression for calculation. Example: A*B + C/100
     */
    private String expression;
    /**
     * Reserved
     */
    private boolean onlyFilling = true;
    /**
     * Reserved
     */
    private boolean onlyChecking = true;
}
