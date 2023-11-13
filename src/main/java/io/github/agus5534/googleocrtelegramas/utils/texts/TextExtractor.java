package io.github.agus5534.googleocrtelegramas.utils.texts;

/**
 * Clase para extraer y procesar texto.
 */
public class TextExtractor {
    private String text;
    private int caracteresMax;

    /**
     * Constructor de la clase.
     *
     * @param text          El texto a procesar.
     * @param caracteresMax La longitud máxima permitida para cada palabra.
     */
    public TextExtractor(String text, int caracteresMax) {
        this.text = text;
        this.caracteresMax = caracteresMax;
    }

    /**
     * Extrae y procesa el texto, eliminando saltos de línea y palabras demasiado largas.
     *
     * @return El texto procesado.
     */
    public String extractText() {
        try {
            text = text != null ? text.replaceAll("\\n", " ") : "";

            StringBuilder extractedText = new StringBuilder();

            for (String word : text.split("\\s+")) {
                if (word.length() <= caracteresMax) {
                    extractedText.append(word).append(" ");
                }
            }

            return extractedText.toString().trim();
        } catch (Exception e) {
            handleTextExtractionException("Error al extraer el texto", e);
            return "";
        }
    }

    /**
     * Maneja una excepción durante la extracción del texto, imprimiendo un mensaje de error.
     *
     * @param errorMessage Mensaje de error.
     * @param e            Excepción ocurrida.
     */
    private void handleTextExtractionException(String errorMessage, Exception e) {
        System.err.println(errorMessage + ": " + e.getMessage());
        e.printStackTrace();
    }
}
