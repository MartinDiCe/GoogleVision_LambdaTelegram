package io.github.agus5534.googleocrtelegramas.utils.files;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import io.github.agus5534.googleocrtelegramas.Main;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.ocr.TextReader;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static io.github.agus5534.googleocrtelegramas.Main.mainFolder;

public class ImageProcessor {

    private static final String TELEGRAMA_PATH = "/telegramas/telegrama-1.tif";

    public static List<Integer> processImages() throws IOException, AnnotateImageException, ImageProcessingException, MetadataException {
        byte[] bytes = getTelegramBytes();

        TimingsReport.report("Telegrama convertido a bytes");

        String outputFolder = mainFolder.getDirectory().getAbsolutePath();
        ImageCropper.setOutputFolder(new File(outputFolder));

        BufferedImage fullImage = ImageIO.read(new ByteArrayInputStream(bytes));

        int numSections = 7;
        return processCroppedImages(fullImage, numSections);
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

    private static List<Integer> processCroppedImages(BufferedImage fullImage, int numSections) throws AnnotateImageException, IOException {
        List<Integer> results = new ArrayList<>();
        BufferedImage[] croppedImages = ImageCropper.cropImageVertically(fullImage, numSections);
        TimingsReport.report("Recorte de imágenes en " + numSections);

        for (BufferedImage croppedImage : croppedImages) {
            int result = TextReader.read(croppedImage);
            results.add(result);
        }

        return results;
    }
}
