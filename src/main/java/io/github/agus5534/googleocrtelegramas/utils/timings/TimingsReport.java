package io.github.agus5534.googleocrtelegramas.utils.timings;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilidad para realizar un seguimiento del tiempo de ejecución y generar informes de tiempos.
 */
public class TimingsReport {

    private static final Logger logger = Logger.getLogger(TimingsReport.class.getName());

    private static Long runtime = System.currentTimeMillis();
    private static Long lastReport = runtime;
    private static LinkedHashMap<String, Long> reports = new LinkedHashMap<>();

    /**
     * Registra el tiempo transcurrido desde el último reporte y almacena la información en la lista de informes.
     *
     * @param subject Descripción del evento o acción que se está cronometrando.
     */
    public static void report(String subject) {
        var current = System.currentTimeMillis();
        var time = current - lastReport;
        reports.put(subject, time);

        lastReport = current;
    }

    /**
     * Construye y registra un informe detallado de los tiempos acumulados.
     */
    public static void buildTimingsReport() {
        try {
            StringBuilder reportsString = new StringBuilder();

            AtomicInteger i = new AtomicInteger(0);
            reports.forEach((s, l) -> {
                reportsString.append("  » ").append(s).append(": ").append(formatDuration(l)).append(i.get() + 1 == reports.size() ? "" : "\n");

                i.getAndIncrement();
            });

            GregorianCalendar cal = new GregorianCalendar(TimeZone.getDefault());
            cal.setTimeInMillis(runtime);

            String message = """
                    -------------TIMINGS REPORT-------------
                    Iniciado: %s
                    Cantidad de Reportes: %s
                    Tiempo Total de Ejecución: %s
                    Reportes:
                    %s
                    ----------------------------------------
                    """.formatted(
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS").format(cal.getTime()),
                    reports.size(),
                    formatDuration(System.currentTimeMillis() - runtime),
                    reportsString.toString()
            );

            logger.log(Level.INFO, message);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al construir el informe de tiempos", e);
        }
    }

    /**
     * Formatea una duración en milisegundos en un formato legible (MM:SS:SSS).
     *
     * @param durationInMillis Duración en milisegundos.
     * @return Representación formateada de la duración.
     */
    private static String formatDuration(long durationInMillis) {
        long minutes = (durationInMillis / (1000 * 60)) % 60;
        long seconds = (durationInMillis / 1000) % 60;
        long milliseconds = durationInMillis % 1000;

        return String.format("%02d:%02d:%03d", minutes, seconds, milliseconds);
    }

}
