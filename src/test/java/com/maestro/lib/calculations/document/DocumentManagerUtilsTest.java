package com.maestro.lib.calculations.document;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentManagerUtilsTest {
    @ParameterizedTest
    @MethodSource("providerForDocumentManager")
    void validate(final List<DocumentVar> data, final List<ValidateRule> rules) throws ScriptException {
        DocumentManagerUtils docManager = new DocumentManagerUtils(data, rules);
        final List<ValidateError> errors = docManager.validate();
        errors.forEach(e -> System.out.println(e.getStatus() + " " + e.getMessage()));
    }

    @ParameterizedTest(name = "#{index} - Test with 'rule': {1}, answer: {2}")
    @MethodSource("providerForExecRule")
    void validate( final List<DocumentVar> data, final String rule, final int respond) throws ScriptException {
        DocumentManagerUtils docManager = new DocumentManagerUtils(data, new ArrayList<>());
        Object obj = docManager.execRule(new ValidateRule("*", "=", rule, true, true));
        assertEquals((double)obj, respond);
    }

    static Stream<Arguments> providerForDocumentManager() {
        final List<ValidateRule> rules = Arrays.asList(
                new ValidateRule("FIELD100", "=", "^FIELD1*^FIELD2 + ^FIELD3", true, true),
                new ValidateRule("FIELD101", "=", "^FIELD1 + ^FIELD3", true, true),
                new ValidateRule("FIELD102", "=", "^FIELD1 - ^FIELD3", true, true),
                new ValidateRule("FIELD200", "=", "Math.SUM(^FIELD101,^FIELD102,^FIELD103)", true, true),
                new ValidateRule("FIELD201", "=", "Math.MIN(^FIELD101,^FIELD102,^FIELD103)", true, true),
                new ValidateRule("FIELD202", "=", "Math.MAX(^FIELD101,^FIELD102,^FIELD103)", true, true)
        );
        return Stream.of(
                arguments(Arrays.asList(
                            new DocumentVar("FIELD1", 0, 0, 100),
                            new DocumentVar("FIELD2", 0, 0, 2),
                            new DocumentVar("FIELD3", 0, 0, 50),
                            new DocumentVar("FIELD100", 0, 0, 250),
                            new DocumentVar("FIELD101", 0, 0, 150),
                            new DocumentVar("FIELD102", 0, 0, 50),
                            new DocumentVar("FIELD200", 0, 0, 450),
                            new DocumentVar("FIELD201", 0, 0, 50),
                            new DocumentVar("FIELD202", 0, 0, 250)
                        ),
                        rules
                )
        );
    }

    static Stream<Arguments> providerForExecRule() {
        final List<DocumentVar> data = Arrays.asList(
                new DocumentVar("FIELD1", 0, 0, 100),
                new DocumentVar("FIELD2", 0, 0, 20),
                new DocumentVar("FIELD3", 0, 0, 500)
        );
        return Stream.of(
                    // Scenario - 1
                arguments(data, "^FIELD1 + ^FIELD3", 600),
                    // Scenario - 2
                arguments(data, "^FIELD1*^FIELD2 + ^FIELD3", 2500),
                    // Scenario - 3
                arguments(data, "^FIELD1 - ^FIELD2", 80),
                // Scenario - 4
                arguments(data, "Math.SUM(^FIELD1,^FIELD2,^FIELD3)", 620),
                // Scenario - 5
                arguments(data, "Math.MIN(^FIELD1,^FIELD2,^FIELD3)", 20),
                // Scenario - 5
                arguments(data, "Math.MAX(^FIELD1,^FIELD2,^FIELD3)", 500),
                // Scenario - 6
                arguments(data, "(^FIELD3 - ^FIELD1)/^FIELD2", 20),
                // Scenario - 7
                arguments(data, "(^FIELD3 > ^FIELD1) ? (^FIELD3-^FIELD1) : (^FIELD1-^FIELD3)", 400)
        );
    }
}