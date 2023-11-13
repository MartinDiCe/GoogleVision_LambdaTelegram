package io.github.agus5534.googleocrtelegramas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.ocr.TextReader;
import io.github.agus5534.googleocrtelegramas.utils.files.FileCreator;
import io.github.agus5534.googleocrtelegramas.utils.files.ImageCropper;
import io.github.agus5534.googleocrtelegramas.utils.files.ImageProcessor;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static void main(String[] args) throws IOException, AnnotateImageException, ImageProcessingException, MetadataException {
        if (!debugMode) {
            return;
        }

        List<Integer> results = ImageProcessor.processImages();

        System.out.println("Resultados: " + results);

        TimingsReport.buildTimingsReport();
    }

}

