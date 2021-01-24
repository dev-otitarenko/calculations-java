package com.maestro.lib.calculations.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentVar {
    private String field; // field name
    private int tabn; // table number
    private int nrow; // row number
    private Object val; // value
}
