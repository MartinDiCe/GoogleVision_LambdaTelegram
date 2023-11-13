package io.github.mdicep.googleocrtelegramas.utils.files;

import com.drew.imaging.ImageProcessingException;

import java.awt.Point;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Clase que proporciona métodos para recortar y procesar imágenes.
 */
public class ImageCropper {

    private static File outputFolder;
    private static final double BORDER_DISCARD_PERCENTAGE = 1.0;

    /**
     * Establece la carpeta de salida para guardar las imágenes procesadas.
     *
     * @param folder Carpeta de salida.
     */
    public static void setOutputFolder(File folder) {
        outputFolder = folder;
    }

    /**
     * Divide una imagen verticalmente en varias secciones y aplica varios procesamientos a cada sección.
     *
     * @param fullImage   Imagen completa a procesar.
     * @param numSections Número de secciones en las que dividir la imagen.
     * @return Arreglo de imágenes procesadas.
     * @throws IllegalArgumentException Si la carpeta de salida no está configurada.
     */
    public static BufferedImage[] cropImageVertically(BufferedImage fullImage, int numSections) throws ImageProcessingException {
        if (outputFolder == null) {
            throw new IllegalArgumentException("La carpeta de salida no ha sido configurada. "
                    + "Configura la carpeta de salida utilizando setOutputFolder antes de llamar a este método.");
        }

        int imageWidth = fullImage.getWidth();
        int imageHeight = fullImage.getHeight();
        int sectionHeight = imageHeight / numSections;

        BufferedImage[] croppedImages = new BufferedImage[numSections];

        for (int i = 0; i < numSections; i++) {
            int y = i * sectionHeight;
            int height = (i == numSections - 1) ? imageHeight - y : sectionHeight;

            int discardTop = (int) (height * BORDER_DISCARD_PERCENTAGE / 100.0);
            int discardBottom = (int) (height * BORDER_DISCARD_PERCENTAGE / 100.0);

            BufferedImage croppedImage = fullImage.getSubimage(0, y + discardTop, imageWidth, height - discardTop - discardBottom);

            processImage(croppedImage);

            croppedImages[i] = croppedImage;
        }

        saveImages(croppedImages);

        return croppedImages;
    }

    /**
     * Procesa una imagen aplicando varios ajustes.
     *
     * @param image Imagen a procesar.
     */
    private static void processImage(BufferedImage image) {
        cropSides(image, 5, 50);
        invertColors(image);
        enhanceBlackColors(image, 200);
        reduceNoise(image);
        removeBlackLines(image, 50, 60);
        invertColors(image);
        removeBackgroundNoise(image, 2, 40);
        invertColors(image);
        enhanceBlackColors(image, 800);
    }

    /**
     * Recorta los lados izquierdo y derecho de una imagen.
     *
     * @param image     Imagen a recortar.
     * @param leftCrop  Píxeles a recortar del lado izquierdo.
     * @param rightCrop Píxeles a recortar del lado derecho.
     */
    private static void cropSides(BufferedImage image, int leftCrop, int rightCrop) {
        int newWidth = image.getWidth() - leftCrop - rightCrop;
        int y = 0;
        int height = image.getHeight();

        BufferedImage croppedImage = new BufferedImage(newWidth, height, image.getType());
        Graphics2D g = croppedImage.createGraphics();
        g.drawImage(image, 0, 0, newWidth, height, leftCrop, y, leftCrop + newWidth, y + height, null);
        g.dispose();

        image.setData(croppedImage.getData());
    }

    /**
     * Invierte los colores de una imagen.
     *
     * @param image Imagen a la que se le invertirán los colores.
     */
    private static void invertColors(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);

                color = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());

                image.setRGB(x, y, color.getRGB());
            }
        }
    }

    /**
     * Elimina el ruido de fondo en una imagen.
     *
     * @param image          Imagen a procesar.
     * @param noiseThreshold Umbral de píxeles oscuros que se considerarán ruido.
     * @param areaThreshold  Umbral del área alrededor de un píxel que se considerará ruido.
     */
    private static void removeBackgroundNoise(BufferedImage image, int noiseThreshold, int areaThreshold) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int darkPixelCount = countDarkPixelsAround(image, x, y, noiseThreshold);

                if (darkPixelCount >= areaThreshold) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
    }

    /**
     * Cuenta el número de píxeles oscuros alrededor de un píxel específico en una imagen.
     *
     * @param image          Imagen a procesar.
     * @param centerX        Coordenada x del píxel central.
     * @param centerY        Coordenada y del píxel central.
     * @param noiseThreshold Umbral de píxeles oscuros.
     * @return Número de píxeles oscuros alrededor del píxel central.
     */
    private static int countDarkPixelsAround(BufferedImage image, int centerX, int centerY, int noiseThreshold) {
        int darkPixelCount = 0;

        for (int y = centerY - 1; y <= centerY + 1; y++) {
            for (int x = centerX - 1; x <= centerX + 1; x++) {
                if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                    int rgb = image.getRGB(x, y);
                    Color color = new Color(rgb, true);

                    if (color.getRed() <= noiseThreshold && color.getGreen() <= noiseThreshold && color.getBlue() <= noiseThreshold) {
                        darkPixelCount++;
                    }
                }
            }
        }

        return darkPixelCount;
    }

    /**
     * Mejora los colores negros en una imagen mediante la reducción del valor de los píxeles oscuros.
     *
     * @param image           Imagen a procesar.
     * @param enhancementValue Valor de mejora para los colores oscuros.
     */
    private static void enhanceBlackColors(BufferedImage image, int enhancementValue) {
        try {
            int blackThreshold = 50;

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgb = image.getRGB(x, y);
                    Color color = new Color(rgb, true);

                    if (color.getRed() < blackThreshold && color.getGreen() < blackThreshold && color.getBlue() < blackThreshold) {
                        color = new Color(
                                Math.max(0, Math.min(255, color.getRed() - enhancementValue)),
                                Math.max(0, Math.min(255, color.getGreen() - enhancementValue)),
                                Math.max(0, Math.min(255, color.getBlue() - enhancementValue))
                        );

                        image.setRGB(x, y, color.getRGB());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error al mejorar los colores negros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reduce el ruido en una imagen ajustando píxeles con pocos píxeles oscuros en su entorno.
     *
     * @param image Imagen a procesar.
     */
    private static void reduceNoise(BufferedImage image) {
        int noiseThreshold = 20; // Puedes ajustar este umbral según tus necesidades

        for (int y = 1; y < image.getHeight() - 1; y++) {
            for (int x = 1; x < image.getWidth() - 1; x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);

                int surroundingPixels = getSurroundingPixelsCount(image, x, y, noiseThreshold);

                if (surroundingPixels < 5) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
    }

    /**
     * Cuenta el número de píxeles oscuros alrededor de un píxel específico en una imagen.
     *
     * @param image          Imagen a procesar.
     * @param x              Coordenada x del píxel central.
     * @param y              Coordenada y del píxel central.
     * @param noiseThreshold Umbral de píxeles oscuros.
     * @return Número de píxeles oscuros alrededor del píxel central.
     */
    private static int getSurroundingPixelsCount(BufferedImage image, int x, int y, int noiseThreshold) {
        int count = 0;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int rgb = image.getRGB(x + i, y + j);
                Color color = new Color(rgb, true);

                if (color.getRed() < noiseThreshold && color.getGreen() < noiseThreshold && color.getBlue() < noiseThreshold) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Elimina líneas negras de una imagen basándose en el ancho y umbral proporcionados.
     *
     * @param image     Imagen a procesar.
     * @param lineWidth Ancho de las líneas negras.
     * @param threshold Umbral para considerar un píxel negro.
     */
    private static void removeBlackLines(BufferedImage image, int lineWidth, int threshold) {
        ArrayList<Point> pixelsToAdjust = new ArrayList<>();

        for (int y = 0; y < image.getHeight(); y++) {
            int blackCount = 0;
            boolean inBlackLine = false;

            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);

                if (color.getRed() < threshold && color.getGreen() < threshold && color.getBlue() < threshold) {
                    blackCount++;

                    if (!inBlackLine) {
                        inBlackLine = true;
                    }
                } else {
                    if (inBlackLine) {
                        if (blackCount >= lineWidth) {
                            for (int i = 0; i < blackCount; i++) {
                                pixelsToAdjust.add(new Point(x - i, y));
                            }
                        }
                        inBlackLine = false;
                        blackCount = 0;
                    }
                }
            }

            if (inBlackLine && blackCount >= lineWidth) {
                for (int i = 0; i < blackCount; i++) {
                    pixelsToAdjust.add(new Point(image.getWidth() - 1 - i, y));
                }
            }
        }

        for (Point pixel : pixelsToAdjust) {
            image.setRGB(pixel.x, pixel.y, Color.WHITE.getRGB());
        }

        pixelsToAdjust.clear();

        for (int x = 0; x < image.getWidth(); x++) {
            int blackCount = 0;
            boolean inBlackLine = false;

            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);

                if (color.getRed() < threshold && color.getGreen() < threshold && color.getBlue() < threshold) {
                    blackCount++;

                    if (!inBlackLine) {
                        inBlackLine = true;
                    }
                } else {
                    if (inBlackLine) {
                        if (blackCount >= lineWidth) {
                            for (int i = 0; i < blackCount; i++) {
                                pixelsToAdjust.add(new Point(x, y - i));
                            }
                        }
                        inBlackLine = false;
                        blackCount = 0;
                    }
                }
            }

            if (inBlackLine && blackCount >= lineWidth) {
                for (int i = 0; i < blackCount; i++) {
                    pixelsToAdjust.add(new Point(x, image.getHeight() - 1 - i));
                }
            }
        }

        for (Point pixel : pixelsToAdjust) {
            image.setRGB(pixel.x, pixel.y, Color.WHITE.getRGB());
        }
    }

    /**
     * Guarda las imágenes procesadas en la carpeta de salida.
     *
     * @param images Imágenes a guardar.
     * @throws ImageProcessingException Si ocurre un error al guardar las imágenes.
     */
    private static void saveImages(BufferedImage[] images) throws ImageProcessingException {
        for (int i = 0; i < images.length; i++) {
            try {
                File outputfile = new File(outputFolder + "/cropped_image_" + i + ".jpg");
                ImageIO.write(images[i], "jpg", outputfile);
            } catch (IOException e) {
                String errorMessage = "Error al guardar la imagen procesada " + i + ": " + e.getMessage();
                throw new ImageProcessingException(errorMessage, e);
            }
        }
    }

}
