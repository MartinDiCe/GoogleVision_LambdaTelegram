package io.github.agus5534.googleocrtelegramas.utils.files;

import com.drew.imaging.ImageProcessingException;
import io.github.agus5534.googleocrtelegramas.Main;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.ocr.TextReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.github.agus5534.googleocrtelegramas.Main.mainFolder;

public class ImageProcessor {

    private static final String TELEGRAMA_PATH = getRandomTelegramaPath();

    private static String getRandomTelegramaPath() {
        int randomTelegramNumber = new Random().nextInt(21) + 1;
        return String.format("/telegramas/telegrama-%d.tif", randomTelegramNumber);
    }

    /**
     * Procesa las imágenes y devuelve los resultados.
     *
     * @return Lista de resultados.
     * @throws IOException              Si hay un error de lectura o escritura.
     * @throws AnnotateImageException   Si hay un error en la anotación de la imagen.
     * @throws ImageProcessingException Si hay un error en el procesamiento de la imagen.
     */
    public static List<Integer> processImages() throws IOException, AnnotateImageException, ImageProcessingException {
        try {

            byte[] bytes = getTelegramBytes();

            String outputFolder = mainFolder.getDirectory().getAbsolutePath();
            ImageCropper.setOutputFolder(new File(outputFolder));

            BufferedImage fullImage = ImageIO.read(new ByteArrayInputStream(bytes));

            int numSections = 7;
            List<Integer> results = processCroppedImages(fullImage, numSections);

            return results;

        } catch (Exception e) {
            handleProcessingException("Error al procesar las imágenes", e);
            return new ArrayList<>();
        }
    }

    private static byte[] getTelegramBytes() {
        var finalURL = Main.class.getResource(TELEGRAMA_PATH);
        try {
            assert finalURL != null;
            return Files.readAllBytes(new File(finalURL.toURI()).toPath());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Ha ocurrido un error al buscar los resources.", e);
        }
    }

    /**
     * Procesa las imágenes recortadas verticalmente y devuelve los resultados.
     *
     * @param fullImage    Imagen completa de la que se obtendrán las secciones recortadas.
     * @param numSections  Número de secciones en las que se recortará la imagen.
     * @return Lista de resultados obtenidos al interpretar el texto en cada sección.
     * @throws AnnotateImageException   Si hay un error en la anotación de la imagen.
     * @throws IOException              Si hay un error de lectura o escritura.
     * @throws ImageProcessingException Si hay un error en el procesamiento de la imagen.
     */
    private static List<Integer> processCroppedImages(BufferedImage fullImage, int numSections)
            throws AnnotateImageException, IOException, ImageProcessingException {
        try {

            List<Integer> results = new ArrayList<>();
            BufferedImage[] croppedImages = ImageCropper.cropImageVertically(fullImage, numSections);

            for (BufferedImage croppedImage : croppedImages) {
                int result = TextReader.read(croppedImage);
                results.add(result);
            }

            return results;

        } catch (Exception e) {
            handleProcessingException("Error al procesar las imágenes recortadas", e);
            return new ArrayList<>();
        }
    }

    private static void handleProcessingException(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}
