package io.github.agus5534.googleocrtelegramas.utils.texts;

import java.util.List;
import java.util.stream.Collectors;

public class TextConcatenator {

    public static String concatenateAndClean(List<String> textList) {
        return textList.stream()
                .map(TextConcatenator::cleanText)
                .collect(Collectors.joining(" "));
    }

    private static String cleanText(String text) {

        return text.replaceAll("\\r?\\n|\\s+", " ");
    }
}
