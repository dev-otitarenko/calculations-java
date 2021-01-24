package com.maestro.lib.calculations;

import com.maestro.lib.calculations.eval.EvalExEngineUtils;
import com.maestro.lib.calculations.eval.EvalExOperator;
import org.junit.jupiter.api.Test;

import com.maestro.lib.calculations.eval.EvalExFunction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EvalExEngineUtilsTest {
    @Test
    public void testGeneralFeatures() {
        BigDecimal result;
        EvalExEngineUtils expression = new EvalExEngineUtils("1+1/3");
        result = expression.eval();

        System.out.println("Res1: " + result);
        assertEquals(result, BigDecimal.valueOf(1.333333));

        expression.setPrecision(3);
        result = expression.eval();

        System.out.println("Res1: " + result);
        assertEquals(result, BigDecimal.valueOf(1.33));

        result = new EvalExEngineUtils("(3.4 + -4.1)/2").eval();
        System.out.println(result);
        assertEquals(result, BigDecimal.valueOf(-0.35));

        result = new EvalExEngineUtils("SQRT(a^2 + b^2)")
                .with("a","5")
                .and("b","4").eval();
        System.out.println(result);
        assertEquals(result, BigDecimal.valueOf(6.4031242));


        BigDecimal a = new BigDecimal("5");
        BigDecimal b = new BigDecimal("4");
        result = new EvalExEngineUtils("SQRT(a^2 + b^2)").with("a",a).and("b",b).eval();

        System.out.println(result);
        assertEquals(result, BigDecimal.valueOf(6.4031242));


        result = new EvalExEngineUtils("7.9000222/PI")
                .setPrecision(10)
                .setRoundingMode(RoundingMode.UP)
                .eval();
        System.out.println(result);
        assertEquals(result, BigDecimal.valueOf(2.514655168));

        result = new EvalExEngineUtils("random() > 0.5").eval();
        System.out.println(result);

        result = new EvalExEngineUtils("not(x<7 || sqrt(max(x,9)) <= 3)").with("x","22.9").eval();
        System.out.println(result);
        assertEquals(result, BigDecimal.valueOf(1));
    }

    @Test
    public void testMismatchedParentheses() {
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> {
                    BigDecimal result = new EvalExEngineUtils("SQRT(a^2 + b^2")
                            .with("a", "5")
                            .and("b", "4").eval();
                });
        assertNotNull(thrown);
        assertTrue(thrown.getMessage().contains("Mismatched parentheses"));
    }

    @Test
    public void testCustomOperator() {
        EvalExEngineUtils e = new EvalExEngineUtils("2.1234 >> 2");
        e.addOperator(new EvalExOperator(">>", 30, true) {
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                System.out.println("\t hello from custom operator: " + v1 + " " + v2);
                return v1.movePointRight(v2.toBigInteger().intValue());
            }
        });
        BigDecimal result = e.eval();

        System.out.println(result);
        assertEquals(result, BigDecimal.valueOf(212.34));
    }

    @Test
    public void testCustomFunction() {
        EvalExEngineUtils e = new EvalExEngineUtils("2 * average(12,4,8)");
        e.addFunction(new EvalExFunction("average", 3) {
            public BigDecimal eval(List<BigDecimal> parameters) {
                 BigDecimal sum = parameters.get(0).add(parameters.get(1)).add(parameters.get(2));
                return sum.divide(new BigDecimal(3));
            }
        });
        BigDecimal result = e.eval();

        System.out.println(result);
        assertEquals(result, BigDecimal.valueOf(16));
    }
}