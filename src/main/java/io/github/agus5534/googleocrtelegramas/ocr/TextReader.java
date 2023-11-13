package io.github.agus5534.googleocrtelegramas.ocr;

import com.google.protobuf.ByteString;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.utils.texts.StringToNumberConverter;
import io.github.agus5534.googleocrtelegramas.utils.texts.TextCleaner;
import io.github.agus5534.googleocrtelegramas.utils.texts.TextExtractor;

import javax.imageio.ImageIO;

/**
 * Clase que realiza la lectura de texto de una imagen utilizando la API de Google Cloud Vision.
 */
public class TextReader {

    /**
     * Lee el texto de una imagen y realiza el procesamiento.
     *
     * @param image La imagen de la cual se extraerá el texto.
     * @return El resultado del procesamiento del texto.
     * @throws IOException             Si ocurre un error de entrada/salida durante la ejecución.
     * @throws AnnotateImageException  Si ocurre un error durante la anotación de la imagen.
     */
    public static int read(BufferedImage image) throws IOException, AnnotateImageException {
        List<String> textos;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpeg", baos);
            ByteString imgBytes = ByteString.copyFrom(baos.toByteArray());

            textos = GoogleCloudVisionAPI.performTextDetection(imgBytes);
        }

        String textoAProcesar = TextCleaner.concatenateAndClean(textos);
        return procesarTextos(textoAProcesar);
    }

    /**
     * Procesa los textos extraídos.
     *
     * @param textos Texto a procesar.
     * @return El resultado del procesamiento del texto.
     */
    private static int procesarTextos(String textos) {
        TextExtractor textExtractor = new TextExtractor(textos, 5);
        int var = StringToNumberConverter.convert(textExtractor.extractText());

        return var;
    }
}

