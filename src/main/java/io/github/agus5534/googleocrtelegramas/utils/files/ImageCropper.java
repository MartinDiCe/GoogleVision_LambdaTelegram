package io.github.agus5534.googleocrtelegramas.utils.files;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class ImageCropper {

    private static File outputFolder;
    private static final double BORDER_DISCARD_PERCENTAGE = 1.0;

    public static void setOutputFolder(File folder) {
        outputFolder = folder;
    }

    public static BufferedImage[] cropImageVertically(BufferedImage fullImage, int numSections) {
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

            cropSides(croppedImage,5,50);

            invertColors(croppedImage);

            enhanceBlackColors(croppedImage, 200);

            reduceNoise(croppedImage);

            removeBlackLines(croppedImage,50,60,30);

            invertColors(croppedImage);

            removeBackgroundNoise(croppedImage,2,40);

            invertColors(croppedImage);

            enhanceBlackColors(croppedImage, 800);

            croppedImages[i] = croppedImage;

        }

        if (outputFolder != null) {
            saveImages(croppedImages);
        } else {
            System.out.println("Error: outputFolder no ha sido configurado. No se guardarán las imágenes.");
        }

        return croppedImages;
    }

    private static BufferedImage cropSides(BufferedImage image, int leftCrop, int rightCrop) {
        int newWidth = image.getWidth() - leftCrop - rightCrop;
        int x = leftCrop;
        int y = 0;
        int width = newWidth;
        int height = image.getHeight();

        BufferedImage croppedImage = new BufferedImage(width, height, image.getType());
        Graphics2D g = croppedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, x, y, x + width, y + height, null);
        g.dispose();

        // Actualizar la imagen original con la imagen recortada
        image.setData(croppedImage.getData());

        return croppedImage;
    }

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

    private static void removeBackgroundNoise(BufferedImage image, int noiseThreshold, int areaThreshold) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int darkPixelCount = countDarkPixelsAround(image, x, y, noiseThreshold);

                // Si el área alrededor del píxel tiene suficientes píxeles oscuros, convertir a blanco
                if (darkPixelCount >= areaThreshold) {
                    image.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
    }

    private static int countDarkPixelsAround(BufferedImage image, int centerX, int centerY, int noiseThreshold) {
        int darkPixelCount = 0;

        for (int y = centerY - 1; y <= centerY + 1; y++) {
            for (int x = centerX - 1; x <= centerX + 1; x++) {
                if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                    int rgb = image.getRGB(x, y);
                    Color color = new Color(rgb, true);

                    // Contar píxeles suficientemente oscuros
                    if (color.getRed() <= noiseThreshold && color.getGreen() <= noiseThreshold && color.getBlue() <= noiseThreshold) {
                        darkPixelCount++;
                    }
                }
            }
        }

        return darkPixelCount;
    }



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

    private static void removeBlackLines(BufferedImage image, int lineWidth, int threshold, int maxGap) {
        ArrayList<Point> pixelsToAdjust = new ArrayList<>();

        // Buscar líneas negras horizontales
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

        // Ajustar los píxeles encontrados horizontalmente
        for (Point pixel : pixelsToAdjust) {
            image.setRGB(pixel.x, pixel.y, Color.WHITE.getRGB());
        }

        // Limpiar la lista para la siguiente revisión
        pixelsToAdjust.clear();

        // Buscar líneas negras verticales
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

        // Ajustar los píxeles encontrados verticalmente
        for (Point pixel : pixelsToAdjust) {
            image.setRGB(pixel.x, pixel.y, Color.WHITE.getRGB());
        }
    }


    private static void saveImages(BufferedImage[] images) {
        for (int i = 0; i < images.length; i++) {
            try {
                File outputfile = new File(outputFolder + "/cropped_image_" + i + ".jpg");
                ImageIO.write(images[i], "jpg", outputfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
