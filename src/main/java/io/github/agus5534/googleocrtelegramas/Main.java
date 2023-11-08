package io.github.agus5534.googleocrtelegramas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.github.agus5534.googleocrtelegramas.utils.files.FileCreator;
import io.github.agus5534.googleocrtelegramas.utils.timings.TimingsReport;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Main implements RequestHandler<Map<String,String>, String> {
    public static FileCreator mainFolder = new FileCreator(new File(System.getProperty("user.home")), "elecciones-tests/");
    public static FileCreator sortedPolygons = new FileCreator(mainFolder.getFile(), "sortedPolygons.txt");
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
    public static void main(String[] args) {
        if(!debugMode) {
            return;
        }

        String tel = "/telegramas/telegrama-"+ ThreadLocalRandom.current().nextInt(1, 21) + ".tif"; // En caso de agregar más, renombrar a telX.tiff y mover el bound de 5 por uno más por cada archivo

        TimingsReport.report("Seleccionado Telegrama");

        var finalURL = Main.class.getResource(tel);

        byte[] bytes;
        try {
            assert finalURL != null;
            bytes = Files.readAllBytes(new File(finalURL.toURI()).toPath());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Ha ocurrido un error al buscar los resources, ¿Están bien cargados? ¿Está marcado el directorio como Resources Root?", e);
        }

        System.out.println("USANDO TELEGRAMA: " + tel);
        TimingsReport.report("Telegrama convertido a bytes");

        TelegramaDownloader.download(bytes);

        TimingsReport.buildTimingsReport();
    }
}
