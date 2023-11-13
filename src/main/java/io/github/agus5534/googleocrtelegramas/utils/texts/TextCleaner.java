package io.github.agus5534.googleocrtelegramas.utils.texts;

import java.util.List;

/**
 * Clase para limpiar y procesar texto.
 */
public class TextCleaner {

    /**
     * Concatena y limpia la lista de textos, encontrando la subcadena más larga.
     *
     * @param textList Lista de textos a concatenar y limpiar.
     * @return La subcadena más larga después de la limpieza, o una cadena vacía si la lista es nula o vacía.
     */
    public static String concatenateAndClean(List<String> textList) {
        try {
            return textList.stream()
                    .map(TextCleaner::cleanText)
                    .map(TextCleaner::findLongestSubstring)
                    .findFirst()
                    .orElse("");
        } catch (Exception e) {
            handleTextProcessingException("Error al procesar el texto", e);
            return "";
        }
    }

    /**
     * Limpia el texto eliminando espacios adicionales y saltos de línea.
     *
     * @param text Texto a limpiar.
     * @return El texto limpio.
     */
    private static String cleanText(String text) {
        return text != null ? text.replaceAll("\\s+", " ").replaceAll("\\n", " ").trim() : "";
    }

    /**
     * Encuentra la subcadena más larga en el texto después de dividirlo en palabras.
     *
     * @param text Texto del cual encontrar la subcadena más larga.
     * @return La subcadena más larga, o una cadena vacía si el texto es nulo o vacío.
     */
    private static String findLongestSubstring(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String[] substrings = text.split("\\s+");
        String longestSubstring = "";

        for (String substring : substrings) {
            if (substring.length() > longestSubstring.length()) {
                longestSubstring = substring;
            }
        }

        return longestSubstring;
    }

    /**
     * Maneja una excepción durante el procesamiento del texto, imprimiendo un mensaje de error.
     *
     * @param errorMessage Mensaje de error.
     * @param e Excepción ocurrida.
     */
    private static void handleTextProcessingException(String errorMessage, Exception e) {
        System.err.println(errorMessage + ": " + e.getMessage());
        e.printStackTrace();
    }
}

