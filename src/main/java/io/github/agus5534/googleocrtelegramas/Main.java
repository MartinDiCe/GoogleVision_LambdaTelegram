package io.github.agus5534.googleocrtelegramas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.ocr.TextReader;
import io.github.agus5534.googleocrtelegramas.utils.files.FileCreator;
import io.github.agus5534.googleocrtelegramas.utils.files.ImageCropper;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Main implements RequestHandler<Map<String,String>, String> {
    public static FileCreator mainFolder = new FileCreator(new File(System.getProperty("user.home")), "elecciones-tests/");
       public static final boolean debugMode = true; // TRUE = USA RESOURCES

    @Override
    public String handleRequest(Map<String,String> event, Context context)  {
        var id = event.get("id");
        var telegramaURLString = event.get("telegramaURL");
        var recurridos = event.get("recurridos");
        var blancos = event.get("blancos");
        var impugnados = event.get("impugnados");
        var nulos = event.get("nulos");

        return ""; //TODO FINISH
    }
    public static void main(String[] args) throws IOException, AnnotateImageException {
        if (!debugMode) {
            return;
        }

        String tel = "/telegramas/telegrama-1.tif";
        var finalURL = Main.class.getResource(tel);

        byte[] bytes;
        try {
            assert finalURL != null;
            bytes = Files.readAllBytes(new File(finalURL.toURI()).toPath());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Ha ocurrido un error al buscar los resources.", e);
        }

        TimingsReport.report("Telegrama convertido a bytes");

        String outputFolder = mainFolder.getDirectory().getAbsolutePath();
        ImageCropper.setOutputFolder(new File(outputFolder));

        BufferedImage fullImage = ImageIO.read(new ByteArrayInputStream(bytes));
        int numSections = 7;

        List<Integer> results = new ArrayList<>();

        for (int i = 0; i < numSections; i++) {
            int y = i * (fullImage.getHeight() / numSections);
            int height = (i == numSections - 1) ? fullImage.getHeight() - y : fullImage.getHeight() / numSections;

            BufferedImage croppedImage = fullImage.getSubimage(0, y, fullImage.getWidth(), height);

            int result = TextReader.read(croppedImage);
            results.add(result);
        }

        System.out.println("Resultados: " + results);
        TimingsReport.buildTimingsReport();
    }

}
