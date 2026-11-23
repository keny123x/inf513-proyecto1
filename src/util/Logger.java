package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void registrarAcceso(String remitente, String asunto, String estado) {
        escribirLinea("access.log", String.format("[%s] REMITENTE: %s | COMANDO: %s | ESTADO: %s",
                LocalDateTime.now().format(FORMATO_FECHA), remitente, asunto, estado));
    }

    public static void registrarError(String origen, String detalle) {
        escribirLinea("error.log", String.format("[%s] ORIGEN: %s | ERROR: %s",
                LocalDateTime.now().format(FORMATO_FECHA), origen, detalle));
    }

    private static synchronized void escribirLinea(String archivo, String linea) {
        try (FileWriter fw = new FileWriter(archivo, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(linea);
        } catch (IOException e) {
            System.err.println("[LOGGER ERROR] No se pudo escribir en " + archivo + ": " + e.getMessage());
        }
    }
}