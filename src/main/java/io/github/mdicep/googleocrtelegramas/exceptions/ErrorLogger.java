package io.github.mdicep.googleocrtelegramas.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.List;

/**
 * Clase para manejar errores y resultados mediante registros.
 */
public class ErrorLogger {

    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(ErrorLogger.class);

    /**
     * Registra un error en el logger especificado o en el logger predeterminado si es nulo.
     *
     * @param logger Logger a utilizar. Si es nulo, se utiliza el logger predeterminado.
     * @param e      Excepción que se registrará.
     */
    public static void logError(LambdaLogger logger, Exception e) {
        if (logger == null) {
            logger = new LambdaLoggerAdapter(DEFAULT_LOGGER);
        }

        logger.log("Error durante el procesamiento de la imagen:");
        logger.log("Clase y método: " + e.getStackTrace()[0].getClassName() + "." + e.getStackTrace()[0].getMethodName());
        logger.log("Mensaje: " + e.getMessage());
        logger.log("Stack Trace:");
        for (StackTraceElement element : e.getStackTrace()) {
            logger.log(element.toString());
        }
    }

    /**
     * Registra resultados en el logger especificado o imprime en la consola si el logger es nulo.
     *
     * @param logger  Logger a utilizar. No puede ser nulo.
     * @param results Resultados a registrar.
     */
    public static void logResults(LambdaLogger logger, List<Integer> results) {
        if (logger == null) {
            System.out.println("El logger es nulo. No se pueden registrar resultados.");
            return;
        }

        logger.log("Resultados: " + results);
    }

    /**
     * Adaptador para utilizar un objeto {@link LambdaLogger} como un {@link Logger} de SLF4J.
     */
    private static class LambdaLoggerAdapter implements LambdaLogger {

        private final Logger logger;

        /**
         * Constructor que recibe el logger de SLF4J a adaptar.
         *
         * @param logger Logger de SLF4J.
         */
        public LambdaLoggerAdapter(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void log(String message) {
            logger.info(message);
        }

        @Override
        public void log(byte[] message) {
            if (message != null) {
                StringBuilder hexString = new StringBuilder(2 * message.length);
                for (byte b : message) {
                    hexString.append(String.format("%02x", b));
                }
                logger.info("Binary message: " + hexString.toString());
            }
        }

    }
}
