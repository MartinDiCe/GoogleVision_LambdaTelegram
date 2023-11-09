package io.github.agus5534.googleocrtelegramas.utils.texts;

public class TextExtractor {
    private String text;
    private int caracteresMax;

    public TextExtractor(String text, int caracteresMax) {
        this.text = text;
        this.caracteresMax = caracteresMax;
    }

    public String extractText() {
        text = text.replaceAll("\\n", " ");

        StringBuilder extractedText = new StringBuilder();

        for (String word : text.split("\\s+")) {
            if (word.length() <= caracteresMax) {
                extractedText.append(word).append(" ");
            }
        }

        return extractedText.toString().trim();
    }
}
