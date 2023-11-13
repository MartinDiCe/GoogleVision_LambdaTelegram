package io.github.mdicep.googleocrtelegramas.exceptions;

/**
 * Excepción lanzada cuando hay un error en la anotación de una imagen.
 */
public class AnnotateImageException extends Exception {
    public AnnotateImageException(String message) {
        super(message);
    }
}