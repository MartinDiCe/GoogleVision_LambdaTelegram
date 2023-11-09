package io.github.agus5534.googleocrtelegramas.utils.files;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.awt.Color;

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

            invertColors(croppedImage);

            enhanceBlackColors(croppedImage, 50);

            croppedImages[i] = croppedImage;
        }

        if (outputFolder != null) {
            saveImages(croppedImages);
        } else {
            System.out.println("Error: outputFolder no ha sido configurado. No se guardarán las imágenes.");
        }

        return croppedImages;
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

    private static void enhanceBlackColors(BufferedImage image, int enhancementValue) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);

                color = new Color(255 - color.getRed() + enhancementValue,
                        255 - color.getGreen() + enhancementValue,
                        255 - color.getBlue() + enhancementValue);

                image.setRGB(x, y, color.getRGB());
            }
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
