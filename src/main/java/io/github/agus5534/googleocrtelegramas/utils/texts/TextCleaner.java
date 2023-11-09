package io.github.agus5534.googleocrtelegramas.utils.texts;

import java.util.List;

public class TextCleaner {

    public static String concatenateAndClean(List<String> textList) {
        return textList.stream()
                .map(TextCleaner::cleanText)
                .map(TextCleaner::findLongestSubstring)
                .findFirst()
                .orElse("");
    }

    private static String cleanText(String text) {
        return text.replaceAll("\\s+", " ").replaceAll("\\n", " ").trim();
    }

    private static String findLongestSubstring(String text) {
        String[] substrings = text.split("\\s+");
        String longestSubstring = "";

        for (String substring : substrings) {
            if (substring.length() > longestSubstring.length()) {
                longestSubstring = substring;
            }
        }

        return longestSubstring;
    }
}
