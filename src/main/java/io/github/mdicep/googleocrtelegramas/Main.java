package io.github.mdicep.googleocrtelegramas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.github.mdicep.googleocrtelegramas.exceptions.ErrorLogger;
import io.github.mdicep.googleocrtelegramas.exceptions.ImageProcessorException;
import io.github.mdicep.googleocrtelegramas.utils.files.ImageProcessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase principal que implementa la interfaz RequestHandler para procesar imágenes en Lambda.
 */
public class Main implements RequestHandler<Map<String, String>, Map<String, Integer>> {

    /**
     * Maneja la solicitud Lambda para procesar imágenes y devolver los resultados.
     *
     * @param event   Los datos de la solicitud.
     * @param context El contexto de la ejecución de Lambda.
     * @return Un mapa con los resultados procesados.
     */
    @Override
    public Map<String, Integer> handleRequest(Map<String, String> event, Context context) {
        Map<String, Integer> result = new HashMap<>();

        try {
            String imageUrl = event.get("image");

            List<Integer> results = ImageProcessor.processImage(imageUrl);
            LambdaLogger lambdaLogger = context.getLogger();

            if (results.size() >= 7) {
                Map<String, Integer> resultMap = new HashMap<>();
                resultMap.put("up", results.get(0));
                resultMap.put("lla", results.get(1));
                resultMap.put("nulos", results.get(2));
                resultMap.put("recurridos", results.get(3));
                resultMap.put("impugnados", results.get(4));
                resultMap.put("blancos", results.get(5));
                resultMap.put("total", results.get(6));

                result.put("results", -1);
                lambdaLogger.log("No hay suficientes resultados para procesar.");
            } else {
                result.put("results", results.get(0));
            }
            ErrorLogger.logResults(lambdaLogger, results);
        } catch (ImageProcessorException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
