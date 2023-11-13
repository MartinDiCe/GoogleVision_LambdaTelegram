package io.github.agus5534.googleocrtelegramas.exceptions;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.List;

public class ErrorLogger {

    public static void logError(LambdaLogger logger, Exception e) {
        if (logger == null) {
            e.printStackTrace();
            return;
        }

        logger.log("Error durante el procesamiento de la imagen:");
        logger.log("Clase y método: " + e.getStackTrace()[0].getClassName() + "." + e.getStackTrace()[0].getMethodName());
        logger.log("Mensaje: " + e.getMessage());
        logger.log("Stack Trace:");
        for (StackTraceElement element : e.getStackTrace()) {
            logger.log(element.toString());
        }
    }

    public static void logResults(LambdaLogger logger, List<Integer> results) {
        if (logger == null) {
            throw new IllegalArgumentException("El logger no puede ser nulo para registrar resultados.");
        }

        logger.log("Resultados: " + results);
    }

}