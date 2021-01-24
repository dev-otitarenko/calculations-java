package com.maestro.lib.calculations.eval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;

/**
 * EvalEx - Java Expression Evaluator
 *
 * EvalEx is a handy expression evaluator for Java, that allows to evaluate simple mathematical and boolean expressions.
 * Key Features:
 * ------------
 * Uses BigDecimal for calculation and result
 * Single class implementation, very compact
 * No dependencies to external libraries
 * Precision and rounding mode can be set
 * Supports variables
 * Standard boolean and mathematical operators
 * Standard basic mathematical and boolean functions
 * Custom functions and operators can be added at runtime
 *
 * Supported Operators
 * -------------------
 *   Mathematical Operators
 *   ----------------------
 *   + Additive operator
 *   - Subtraction operator
 *   * Multiplication operator
 *   / Division operator
 *   % Remainder operator (Modulo)
 *   ^ Power operator
 *
 *   Boolean Operators:
 *   -----------------
 *   = Equals
 *   == Equals
 *   != Not equals
 *   <> Not equals
 *   <> Less than
 *   <= Less than or equal to
 *   >  Greater than
 *   >;= Greater than or equal to
 *   && - Boolean and
 *   || - Boolean or
 * P.S. Boolean operators result always in a BigDecimal value of 1 or 0 (zero). Any non-zero value is treated as a _true_ value. Boolean _not_ is implemented by a function.
 * >
 * Supported Functions:
 * --------------------
 *   NOT(<i>expression</i>) - Boolean negation, 1 (means true) if the expression is not zero
 *   IF(<i>condition</i>,<i>value_if_true</i>,<i>value_if_false</i>)
 *      Returns one value if the condition evaluates to true or the other if it evaluates to false
 *   RANDOM()- Produces a random number between 0 and 1
 *   MIN(<i>e1</i>,<i>e2</i>) - Returns the smaller of both expressions
 *   MAX(<i>e1</i>,<i>e2</i>) -Returns the bigger of both expressions
 *   ABS(<i>expression</i>) - Returns the absolute (non-negative) value of the expression
 *   ROUND(<i>expression</i>,precision) - Rounds a value to a certain number of digits, uses the current rounding mode
 *   FLOOR(<i>expression</i>) - Rounds the value down to the nearest integer
 *   CEILING(<i>expression</i>) - Rounds the value up to the nearest integer
 *   LOG(<i>expression</i>) - Returns the natural logarithm (base e) of an expression
 *   SQRT(<i>expression</i>) - Returns the square root of an expression
 *   SIN(<i>expression</i>) - Returns the trigonometric sine of an angle (in degrees)
 *   COS(<i>expression</i>) - Returns the trigonometric cosine of an angle (in degrees)
 *   TAN(<i>expression</i>) - Returns the trigonometric tangens of an angle (in degrees)
 *   INH(<i>expression</i>) - Returns the hyperbolic sine of a value
 *   COSH(<i>expression</i>) - Returns the hyperbolic cosine of a value
 *   TANH(<i>expression</i>) - Returns the hyperbolic tangens of a value
 *   RAD(<i>expression</i>) - Converts an angle measured in degrees to an approximately equivalent angle measured in radians
 *   DEG(<i>expression</i>) - Converts an angle measured in radians to an approximately equivalent angle measured in degrees
 *
 * Supported Constants:
 * --------------------
 * PI - The value of PI, exact to 100 digits
 * TRUE - The value one
 * FALSE - The value zero
 */
public class EvalExEngineUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(EvalExEngineUtils.class);

    /**
     * Definition of PI as a constant, can be used in expressions as variable.
     */
    public static final BigDecimal PI = new BigDecimal(
            "3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679");

    /**
     * The {@link MathContext} to use for calculations.
     */
    private MathContext mc = MathContext.DECIMAL32;

    /**
     * The original infix expression.
     */
    private String expression = null;

    /**
     * The cached RPN (Reverse Polish Notation) of the expression.
     */
    private List<String> rpn = null;

    /**
     * All defined operators with name and implementation.
     */
    private Map<String, EvalExOperator> operators = new HashMap<>();

    /**
     * All defined functions with name and implementation.
     */
    private Map<String, EvalExFunction> functions = new HashMap<>();

    /**
     * All defined variables with name and value.
     */
    private Map<String, BigDecimal> variables = new HashMap<>();

    /**
     * What character to use for decimal separators.
     */
    private final char decimalSeparator = '.';

    /**
     * What character to use for minus sign (negative values).
     */
    private final char minusSign = '-';

     /**
     * Custom Tokenizer that allows to iterate over a {@link String}
     * expression token by token. Blank characters will be skipped.
     */
    private class EvalExTokenizer implements Iterator<String> {
        /**
         * Actual position in expression string.
         */
        private int pos = 0;

        /**
         * The original input expression.
         */
        private String input;
        /**
         * The previous token or <code>null</code> if none.
         */
        private String previousToken;

        /**
         * Creates a new tokenizer for an expression.
         *
         * @param input
         *            The expression string.
         */
        public EvalExTokenizer(String input) {
            this.input = input;
        }

        @Override
        public boolean hasNext() {
            return (pos < input.length());
        }

        /**
         * Peek at the next character, without advancing the iterator.
         *
         * @return The next character or character 0, if at end of string.
         */
        private char peekNextChar() {
            if (pos < (input.length() - 1)) {
                return input.charAt(pos + 1);
            } else {
                return 0;
            }
        }

        @Override
        public String next() {
            StringBuilder token = new StringBuilder();
            if (pos >= input.length()) {
                return previousToken = null;
            }
            char ch = input.charAt(pos);
            while (Character.isWhitespace(ch) && pos < input.length()) {
                ch = input.charAt(++pos);
            }
            if (Character.isDigit(ch)) {
                while ((Character.isDigit(ch) || ch == decimalSeparator)
                        && (pos < input.length())) {
                    token.append(input.charAt(pos++));
                    ch = pos == input.length() ? 0 : input.charAt(pos);
                }
            } else if (ch == minusSign
                    && Character.isDigit(peekNextChar())
                    && ("(".equals(previousToken) || ",".equals(previousToken)
                    || previousToken == null || operators
                    .containsKey(previousToken))) {
                token.append(minusSign);
                pos++;
                token.append(next());
            } else if (Character.isLetter(ch)) {
                while ((Character.isLetter(ch) || Character.isDigit(ch) || (ch == '_')) && (pos < input.length())) {
                    token.append(input.charAt(pos++));
                    ch = pos == input.length() ? 0 : input.charAt(pos);
                }
            } else if (ch == '(' || ch == ')' || ch == ',') {
                token.append(ch);
                pos++;
            } else {
                while (!Character.isLetter(ch) && !Character.isDigit(ch)
                        && !Character.isWhitespace(ch) && ch != '('
                        && ch != ')' && ch != ',' && (pos < input.length())) {
                    token.append(input.charAt(pos));
                    pos++;
                    ch = pos == input.length() ? 0 : input.charAt(pos);
                    if (ch == minusSign) {
                        break;
                    }
                }
                if (!operators.containsKey(token.toString())) {
                    throw new EvalExException("Unknown operator '" + token
                            + "' at position " + (pos - token.length() + 1));
                }
            }
            return previousToken = token.toString();
        }

        @Override
        public void remove() {
            throw new EvalExException("remove() not supported");
        }

        /**
         * Get the actual character position in the string.
         *
         * @return The actual character position.
         */
        public int getPos() {
            return pos;
        }
    }

    /**
     * Creates a new expression instance from an expression string.
     *
     * @param expression
     *            The expression. E.g. <code>"2.4*sin(3)/(2-4)"</code> or
     *            <code>"sin(y)>0 & max(z, 3)>3"</code>
     */
    public EvalExEngineUtils(String expression) {
        this.expression = expression;
            // Adding Operations
        addOperator(new EvalExOperator("+", 20, true) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.add(v2, mc);
            }
        });
        addOperator(new EvalExOperator("-", 20, true) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.subtract(v2, mc);
            }
        });
        addOperator(new EvalExOperator("*", 30, true) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.multiply(v2, mc);
            }
        });
        addOperator(new EvalExOperator("/", 30, true) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.divide(v2, mc);
            }
        });
        addOperator(new EvalExOperator("%", 30, true) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.remainder(v2, mc);
            }
        });
        addOperator(new EvalExOperator("^", 40, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                /*-
                 * http://stackoverflow.com/questions/3579779/how-to-do-a-fractional-power-on-bigdecimal-in-java
                 */
                int signOf2 = v2.signum();
                double dn1 = v1.doubleValue();
                v2 = v2.multiply(new BigDecimal(signOf2)); // n2 is now positive
                BigDecimal remainderOf2 = v2.remainder(BigDecimal.ONE);
                BigDecimal n2IntPart = v2.subtract(remainderOf2);
                BigDecimal intPow = v1.pow(n2IntPart.intValueExact(), mc);
                BigDecimal doublePow = new BigDecimal(Math.pow(dn1, remainderOf2.doubleValue()));

                BigDecimal result = intPow.multiply(doublePow, mc);
                if (signOf2 == -1) {
                    result = BigDecimal.ONE.divide(result, mc.getPrecision(),
                            RoundingMode.HALF_UP);
                }
                return result;
            }
        });
        addOperator(new EvalExOperator("&&", 4, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                boolean b1 = !v1.equals(BigDecimal.ZERO);
                boolean b2 = !v2.equals(BigDecimal.ZERO);
                return b1 && b2 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator("||", 2, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                boolean b1 = !v1.equals(BigDecimal.ZERO);
                boolean b2 = !v2.equals(BigDecimal.ZERO);
                return b1 || b2 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator(">", 10, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) == 1 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator(">=", 10, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) >= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator("<", 10, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) == -1 ? BigDecimal.ONE
                        : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator("<=", 10, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) <= 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator("=", 7, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) == 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator("==", 7, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return operators.get("=").eval(v1, v2);
            }
        });

        addOperator(new EvalExOperator("!=", 7, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return v1.compareTo(v2) != 0 ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addOperator(new EvalExOperator("<>", 7, false) {
            @Override
            public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
                return operators.get("!=").eval(v1, v2);
            }
        });
            // Adding functions
        addFunction(new EvalExFunction("NOT", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                boolean zero = parameters.get(0).compareTo(BigDecimal.ZERO) == 0;
                return zero ? BigDecimal.ONE : BigDecimal.ZERO;
            }
        });
        addFunction(new EvalExFunction("IF", 3) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                boolean isTrue = !parameters.get(0).equals(BigDecimal.ZERO);
                return isTrue ? parameters.get(1) : parameters.get(2);
            }
        });
        addFunction(new EvalExFunction("RANDOM", 0) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.random();
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("SIN", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.sin(Math.toRadians(parameters.get(0)
                        .doubleValue()));
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("COS", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.cos(Math.toRadians(parameters.get(0)
                        .doubleValue()));
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("TAN", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.tan(Math.toRadians(parameters.get(0)
                        .doubleValue()));
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("SINH", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.sinh(parameters.get(0).doubleValue());
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("COSH", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.cosh(parameters.get(0).doubleValue());
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("TANH", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.tanh(parameters.get(0).doubleValue());
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("RAD", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.toRadians(parameters.get(0).doubleValue());
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("DEG", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.toDegrees(parameters.get(0).doubleValue());
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("MAX", 2) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal v1 = parameters.get(0);
                BigDecimal v2 = parameters.get(1);
                return v1.compareTo(v2) > 0 ? v1 : v2;
            }
        });
        addFunction(new EvalExFunction("MIN", 2) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal v1 = parameters.get(0);
                BigDecimal v2 = parameters.get(1);
                return v1.compareTo(v2) < 0 ? v1 : v2;
            }
        });
        addFunction(new EvalExFunction("ABS", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                return parameters.get(0).abs(mc);
            }
        });
        addFunction(new EvalExFunction("LOG", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                double d = Math.log(parameters.get(0).doubleValue());
                return new BigDecimal(d, mc);
            }
        });
        addFunction(new EvalExFunction("ROUND", 2) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal toRound = parameters.get(0);
                int precision = parameters.get(1).intValue();
                return toRound.setScale(precision, mc.getRoundingMode());
            }
        });
        addFunction(new EvalExFunction("FLOOR", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal toRound = parameters.get(0);
                return toRound.setScale(0, RoundingMode.FLOOR);
            }
        });
        addFunction(new EvalExFunction("CEILING", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                BigDecimal toRound = parameters.get(0);
                return toRound.setScale(0, RoundingMode.CEILING);
            }
        });
        addFunction(new EvalExFunction("SQRT", 1) {
            @Override
            public BigDecimal eval(List<BigDecimal> parameters) {
                /*
                 * From The Java Programmers Guide To numerical Computing
                 * (Ronald Mak, 2003)
                 */
                BigDecimal x = parameters.get(0);
                if (x.compareTo(BigDecimal.ZERO) == 0) {
                    return new BigDecimal(0);
                }
                if (x.signum() < 0) {
                    throw new EvalExException(
                            "Argument to SQRT() function must not be negative");
                }
                BigInteger n = x.movePointRight(mc.getPrecision() << 1)
                        .toBigInteger();

                int bits = (n.bitLength() + 1) >> 1;
                BigInteger ix = n.shiftRight(bits);
                BigInteger ixPrev;

                do {
                    ixPrev = ix;
                    ix = ix.add(n.divide(ix)).shiftRight(1);
                    // Give other threads a chance to work;
                    Thread.yield();
                } while (ix.compareTo(ixPrev) != 0);

                return new BigDecimal(ix, mc.getPrecision());
            }
        });

        variables.put("PI", PI);
        variables.put("TRUE", BigDecimal.ONE);
        variables.put("FALSE", BigDecimal.ZERO);
    }

    /**
     * Is the string a number?
     *
     * @param st The string.
     * @return true, if the input string is a number.
     */
    private boolean isNumber(String st) {
        if (st.charAt(0) == minusSign && st.length() == 1)
            return false;
        for (char ch : st.toCharArray()) {
            if (!Character.isDigit(ch) && ch != minusSign
                    && ch != decimalSeparator)
                return false;
        }
        return true;
    }

    /**
     * Implementation of the <i>Shunting Yard</i> algorithm to transform an
     * infix expression to a RPN expression.
     *
     * @param expression The input expression in infx.
     * @return A RPN representation of the expression, with each token as a list
     *         member.
     */
    private List<String> shuntingYard(String expression) {
        LOGGER.info("shutingYard: {}", expression);
        List<String> outputQueue = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        EvalExTokenizer tokenizer = new EvalExTokenizer(expression);

        String lastFunction = null;
        String previousToken = null;
        while (tokenizer.hasNext()) {
            String token = tokenizer.next();
            if (isNumber(token)) {
                outputQueue.add(token);
            } else if (variables.containsKey(token)) {
                outputQueue.add(token);
            } else if (functions.containsKey(token.toUpperCase())) {
                stack.push(token);
                lastFunction = token;
            } else if (Character.isLetter(token.charAt(0))) {
                stack.push(token);
            } else if (",".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    outputQueue.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new EvalExException("Parse error for function '"
                            + lastFunction + "'");
                }
            } else if (operators.containsKey(token)) {
                EvalExOperator o1 = operators.get(token);
                String token2 = stack.isEmpty() ? null : stack.peek();
                while (operators.containsKey(token2)
                        && ((o1.isLeftAssoc() && o1.getPrecedence() <= operators
                        .get(token2).getPrecedence()) || (o1
                        .getPrecedence() < operators.get(token2)
                        .getPrecedence()))) {
                    outputQueue.add(stack.pop());
                    token2 = stack.isEmpty() ? null : stack.peek();
                }
                stack.push(token);
            } else if ("(".equals(token)) {
                if (previousToken != null) {
                    if (isNumber(previousToken)) {
                        throw new EvalExException("Missing operator at character position " + tokenizer.getPos());
                    }
                }
                stack.push(token);
            } else if (")".equals(token)) {
                while (!stack.isEmpty() && !"(".equals(stack.peek())) {
                    outputQueue.add(stack.pop());
                }
                if (stack.isEmpty()) {
                    throw new RuntimeException("Mismatched parentheses");
                }
                stack.pop();
                if (!stack.isEmpty()
                        && functions.containsKey(stack.peek().toUpperCase())) {
                    outputQueue.add(stack.pop());
                }
            }
            previousToken = token;
        }
        while (!stack.isEmpty()) {
            String element = stack.pop();
            if ("(".equals(element) || ")".equals(element)) {
                throw new RuntimeException("Mismatched parentheses");
            }
            if (!operators.containsKey(element)) {
                throw new RuntimeException("Unknown operator or function: "
                        + element);
            }
            outputQueue.add(element);
        }
        return outputQueue;
    }

    /**
     * Evaluates the expression.
     *
     * @return The result of the expression.
     */
    public BigDecimal eval() {
        Stack<BigDecimal> stack = new Stack<>();

        for (String token : getRPN()) {
            if (operators.containsKey(token)) {
                BigDecimal v1 = stack.pop();
                BigDecimal v2 = stack.pop();
                stack.push(operators.get(token).eval(v2, v1));
            } else if (variables.containsKey(token)) {
                stack.push(variables.get(token).round(mc));
            } else if (functions.containsKey(token.toUpperCase())) {
                EvalExFunction f = functions.get(token.toUpperCase());
                ArrayList<BigDecimal> p = new ArrayList<>(
                        f.getNumParams());
                for (int i = 0; i < f.getNumParams(); i++) { // numParams
                    p.add(0,stack.pop());
                }
                BigDecimal fResult = f.eval(p);
                stack.push(fResult);
            } else {
                stack.push(new BigDecimal(token, mc));
            }
        }
        return stack.pop().stripTrailingZeros();
    }

    /**
     * Sets the precision for expression evaluation.
     *
     * @param precision The new precision.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils setPrecision(int precision) {
        this.mc = new MathContext(precision);
        return this;
    }

    /**
     * Sets the rounding mode for expression evaluation.
     *
     * @param roundingMode  The new rounding mode.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils setRoundingMode(RoundingMode roundingMode) {
        this.mc = new MathContext(mc.getPrecision(), roundingMode);
        return this;
    }

    /**
     * Adds an operator to the list of supported operators.
     *
     * @param operator The operator to add.
     * @return The previous operator with that name, or <code>null</code> if
     *         there was none.
     */
    public EvalExOperator addOperator(EvalExOperator operator) {
        return operators.put(operator.getOper(), operator);
    }

    /**
     * Adds a function to the list of supported functions
     *
     * @param function The function to add.
     * @return The previous operator with that name, or <code>null</code> if
     *         there was none.
     */
    public EvalExFunction addFunction(EvalExFunction function) {
        return functions.put(function.getName(), function);
    }

    /**
     * Sets a variable value.
     *
     * @param variable The variable name.
     * @param value The variable value.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils setVariable(String variable, BigDecimal value) {
        variables.put(variable, value);
        return this;
    }

    /**
     * Sets a variable value.
     *
     * @param variable The variable to set.
     * @param value The variable value.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils setVariable(String variable, String value) {
        if (isNumber(value))
            variables.put(variable, new BigDecimal(value));
        else {
            expression = expression.replaceAll("\\b" + variable + "\\b", "(" + value + ")");
            rpn = null;
        }
        return this;
    }

    /**
     * Sets a variable value.
     *
     * @param variable The variable to set.
     * @param value The variable value.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils with(String variable, BigDecimal value) {
        return setVariable(variable, value);
    }

    /**
     * Sets a variable value.
     *
     * @param variable The variable to set.
     * @param value The variable value.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils and(String variable, String value) {
        return setVariable(variable, value);
    }

    /**
     * Sets a variable value.
     *
     * @param variable The variable to set.
     * @param value The variable value.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils and(String variable, BigDecimal value) {
        return setVariable(variable, value);
    }

    /**
     * Sets a variable value.
     *
     * @param variable The variable to set.
     * @param value The variable value.
     * @return The expression, allows to chain methods.
     */
    public EvalExEngineUtils with(String variable, String value) {
        return setVariable(variable, value);
    }

    /**
     * Get an iterator for this expression, allows iterating over an expression
     * token by token.
     *
     * @return A new iterator instance for this expression.
     */
    public Iterator<String> getExpressionTokenizer() {
        return new EvalExTokenizer(this.expression);
    }

    /**
     * Cached access to the RPN notation of this expression, ensures only one
     * calculation of the RPN per expression instance. If no cached instance
     * exists, a new one will be created and put to the cache.
     *
     * @return The cached RPN instance.
     */
    private List<String> getRPN() {
        if (rpn == null) {
            rpn = shuntingYard(this.expression);
        }
        return rpn;
    }

    /**
     * Get a string representation of the RPN (Reverse Polish Notation) for this
     * expression.
     *
     * @return A string with the RPN representation for this expression.
     */
    public String toRPN() {
        String result = new String();
        for (String st : getRPN()) {
            result = result.isEmpty() ? result : result + " ";
            result += st;
        }
        return result;
    }
}
