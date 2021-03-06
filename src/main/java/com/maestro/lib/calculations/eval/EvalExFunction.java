package com.maestro.lib.calculations.eval;

import java.math.BigDecimal;
import java.util.List;

/**
 * Abstract definition of a supported expression function. A function is
 * defined by a name, the number of parameters and the actual processing
 * implementation.
 */
public abstract class EvalExFunction {
    /**
     * Name of this function.
     */
    private String name;
    /**
     * Number of parameters expected for this function.
     */
    private int numParams;

    /**
     * Creates a new function with given name and parameter count.
     *
     * @param name The name of the function.
     * @param numParams The number of parameters for this function.
     */
    public EvalExFunction(String name, int numParams) {
        this.name = name.toUpperCase();
        this.numParams = numParams;
    }

    public String getName() {
        return name;
    }

    public int getNumParams() {
        return numParams;
    }

    /**
     * Implementation for this function.
     *
     * @param parameters
     *            Parameters will be passed by the expression evaluator as a
     *            {@link List} of {@link BigDecimal} values.
     * @return The function must return a new {@link BigDecimal} value as a
     *         computing result.
     */
    public abstract BigDecimal eval(List<BigDecimal> parameters);
}
