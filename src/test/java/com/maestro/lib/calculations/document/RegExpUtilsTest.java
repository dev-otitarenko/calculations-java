package com.maestro.lib.calculations.document;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class RegExpUtilsTest {
    @ParameterizedTest(name = "#{index} - Test with 'rule': {0}")
    @MethodSource("providerForRules")
    public void getMatches1(final String rule, final List<String> params) {
        final List<String> fields = RegExpUtils.getMatches(rule, "(\\^\\w+)");

        fields.stream().forEach(f -> System.out.println(f));

        assertEquals(fields.size(), params.size());
        List<String> fields1 = fields.stream().filter(f -> !params.contains(f)).collect(Collectors.toList());
        assertEquals(fields1.size(), 0);
    }

    static Stream<Arguments> providerForRules() {
        return Stream.of(
                arguments("^FIELD1+^FIELD2", Arrays.asList("^FIELD1", "^FIELD2")),
                arguments("Math.MIN(^FIELD1, ^FIELD2)", Arrays.asList("^FIELD1", "^FIELD2"))
        );
    }
}