package com.maestro.lib.calculations.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegExpUtils.class);

    public static String getMatch(String s, String p) {
        // returns first match of p in s for first group in regular expression
        final Matcher m = Pattern.compile(p).matcher(s);
        return m.find() ? m.group(1) : "";
    }

    public static List<String> getMatches(String s, String p) {
        List<String> matches = new ArrayList<>();
        final Matcher m = Pattern.compile(p).matcher(s);
        while(m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }
}
