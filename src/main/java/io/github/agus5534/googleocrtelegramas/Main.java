package io.github.agus5534.googleocrtelegramas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.drew.imaging.ImageProcessingException;
import io.github.agus5534.googleocrtelegramas.exceptions.AnnotateImageException;
import io.github.agus5534.googleocrtelegramas.exceptions.ErrorLogger;
import io.github.agus5534.googleocrtelegramas.utils.files.FileCreator;
import io.github.agus5534.googleocrtelegramas.utils.files.ImageProcessor;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main implements RequestHandler<Map<String, String>, Map<String, Integer>> {
    public static FileCreator mainFolder = new FileCreator(new File(System.getProperty("user.home")), "elecciones-tests/");
    public static final boolean debugMode = true; // TRUE = USA RESOURCES

    /**
     * Maneja la solicitud Lambda para procesar imágenes y devolver los resultados.
     *
     * @param event   Los datos de la solicitud.
     * @param context El contexto de la ejecución de Lambda.
     * @return Un mapa con los resultados procesados.
     */
    public Map<String, Integer> handleRequest(Map<String, String> event, Context context) {
        Map<String, Integer> result = new HashMap<>();

        try {
            List<Integer> results = ImageProcessor.processImages();
            LambdaLogger lambdaLogger = context.getLogger();

            if (results.size() >= 7) {
                result.put("up", results.get(0));
                result.put("lla", results.get(1));
                result.put("nulos", results.get(2));
                result.put("recurridos", results.get(3));
                result.put("impugnados", results.get(4));
                result.put("blancos", results.get(5));
                result.put("total", results.get(6));
            } else {
                lambdaLogger.log("No hay suficientes resultados para procesar.");
            }
            ErrorLogger.logResults(lambdaLogger, results);
        } catch (IOException | AnnotateImageException e) {
            ErrorLogger.logError(context.getLogger(), e);
        } catch (ImageProcessingException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Método principal para ejecutar localmente en modo de depuración.
     *
     * @param args Argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        if (!debugMode) {
            return;
        }
        try {
            List<Integer> results = ImageProcessor.processImages();
            System.out.println("Resultados: " + results);
        } catch (IOException | AnnotateImageException e) {
            e.printStackTrace();
        } catch (ImageProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}