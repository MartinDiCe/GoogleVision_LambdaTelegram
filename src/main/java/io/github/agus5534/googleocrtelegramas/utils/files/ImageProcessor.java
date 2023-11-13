package io.github.agus5534.googleocrtelegramas.utils.files;

import com.drew.imaging.ImageProcessingException;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.exceptions.ImageProcessorException;
import io.github.agus5534.googleocrtelegramas.ocr.TextReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para procesar imágenes y obtener resultados mediante OCR.
 */
public class ImageProcessor {

    /**
     * Carpeta principal para almacenar archivos generados por el procesador de imágenes.
     */
    public static FileCreator mainFolder = new FileCreator(new File(System.getProperty("user.home")), "elecciones-tests/");

    /**
     * Procesa la imagen y devuelve los resultados.
     *
     * @param imageUrl URL de la imagen a procesar.
     * @return Lista de resultados.
     * @throws ImageProcessorException Si hay un error en el procesamiento de la imagen.
     */
    public static List<Integer> processImage(String imageUrl) throws ImageProcessorException {
        try {
            BufferedImage image = loadImageFromUrl(imageUrl);
            String outputFolder = mainFolder.getDirectory().getAbsolutePath();
            ImageCropper.setOutputFolder(new File(outputFolder));

            int numSections = 7;
            return processCroppedImages(image, numSections);
        } catch (IOException | AnnotateImageException | ImageProcessingException e) {
            throw new ImageProcessorException("Error al procesar la imagen", e);
        }
    }

    /**
     * Carga una imagen desde una URL.
     *
     * @param imageUrl URL de la imagen.
     * @return La imagen cargada.
     * @throws IOException Si hay un error al leer la imagen.
     */
    private static BufferedImage loadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        return ImageIO.read(url);
    }

    /**
     * Procesa las imágenes recortadas verticalmente y devuelve los resultados.
     *
     * @param fullImage   Imagen completa de la que se obtendrán las secciones recortadas.
     * @param numSections Número de secciones en las que se recortará la imagen.
     * @return Lista de resultados obtenidos al interpretar el texto en cada sección.
     * @throws AnnotateImageException   Si hay un error en la anotación de la imagen.
     * @throws IOException              Si hay un error de lectura o escritura.
     * @throws ImageProcessingException Si hay un error en el procesamiento de la imagen.
     * @throws ImageProcessorException  Si hay un error general en el procesamiento de la imagen.
     */
    private static List<Integer> processCroppedImages(BufferedImage fullImage, int numSections)
            throws AnnotateImageException, IOException, ImageProcessingException, ImageProcessorException {
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
            throw new ImageProcessorException("Error al procesar las imágenes recortadas", e);
        }
    }

    /**
     * Maneja excepciones durante el procesamiento de imágenes.
     *
     * @param message Mensaje de la excepción.
     * @param e       Excepción original.
     */
    private static void handleProcessingException(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}

