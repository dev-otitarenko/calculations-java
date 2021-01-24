package com.maestro.lib.calculations.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Regexp utils
 */
public class RegExpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegExpUtils.class);

    /**
     *
     * @param s input string
     * @param p regexp pattern
     * @return String
     */
    public static String getMatch(String s, String p) {
        final Matcher m = Pattern.compile(p).matcher(s);
        return m.find() ? m.group(1) : "";
    }

    /**
     *
     * @param s input string
     * @param p regexp patter
     * @return List list of string
     */
    public static List<String> getMatches(String s, String p) {
        List<String> matches = new ArrayList<>();
        final Matcher m = Pattern.compile(p).matcher(s);
        while(m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }
}
