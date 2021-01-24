package com.maestro.lib.calculations.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
/**
 * Class describing row in a data set
 */
public class DocumentVar {
    /**
     * A field name
     */
    private String field;
    /**
     * Table number
     */
    private int tabn;
    /**
     * Row number
     */
    private int nrow;
    /**
     * Value
     */
    private Object val;
}
