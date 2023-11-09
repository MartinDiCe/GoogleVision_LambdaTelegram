package io.github.agus5534.googleocrtelegramas;

import io.github.agus5534.googleocrtelegramas.ocr.TextReader;
import io.github.agus5534.googleocrtelegramas.utils.files.FileCreator;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.UUID;

public class TelegramaDownloader {

    private final byte[] bytes;
    private final FileCreator mesaFolder;

    public static void download(byte[] telegrama) {
        try {
            new TelegramaDownloader(telegrama).downloadData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TelegramaDownloader(byte[] telegrama) {
        this.bytes = telegrama;
        this.mesaFolder = new FileCreator(Main.mainFolder.getFile(), UUID.randomUUID() +"/", true);

        Arrays.stream(mesaFolder.getContents()).forEach(File::delete);

    }

    public void downloadData() throws Exception {

        BufferedImage tiffImage = ImageIO.read(new ByteArrayInputStream(bytes));

        BufferedImage jpegImage = new BufferedImage(
                tiffImage.getWidth(),
                tiffImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        jpegImage.createGraphics().drawImage(tiffImage, 0, 0, Color.WHITE, null);

        TimingsReport.report("Creado .jpg del telegrama");

        try {
            TextReader.read(jpegImage);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
